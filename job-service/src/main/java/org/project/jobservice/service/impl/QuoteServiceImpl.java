package org.project.jobservice.service.impl;

import org.project.jobservice.client.CustomerClient;
import org.project.jobservice.client.NotificationClient;
import org.project.jobservice.client.ScheduleClient;
import org.project.jobservice.domain.JobStatus;
import org.project.jobservice.domain.QuoteStatus;
import org.project.jobservice.dto.CreateQuoteRequest;
import org.project.jobservice.dto.CreateScheduleRequest;
import org.project.jobservice.dto.NotificationRequest;
import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.entity.Job;
import org.project.jobservice.entity.LineItem;
import org.project.jobservice.entity.Quote;
import org.project.jobservice.mapper.JobMapper;
import org.project.jobservice.repository.JobRepository;
import org.project.jobservice.repository.QuoteRepository;
import org.project.jobservice.service.QuoteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    // Feign Clients for Microservice Communication
    private final NotificationClient notificationClient;
    private final ScheduleClient scheduleClient;
    private final CustomerClient customerClient;

    // --- 1. CREATE (Status: REQUESTED) ---
    @Override
    @Transactional
    public QuoteDTO createQuote(CreateQuoteRequest request) {
        // Build the Quote with REQUESTED status
        Quote quote = Quote.builder()
                .customerId(request.customerId())
                .propertyId(request.propertyId())
                .title(request.title())
                .requestDetails(request.requestDetails()) // Store raw customer request (Coords, etc.)
                .status(QuoteStatus.REQUESTED) // Correct initial status
                .createdAt(Instant.now())
                .lineItems(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();

        // Process Line Items
        BigDecimal total = BigDecimal.ZERO;
        if (request.lineItems() != null) {
            for (var itemRequest : request.lineItems()) {
                BigDecimal price = itemRequest.unitPrice() != null ? itemRequest.unitPrice() : BigDecimal.ZERO;
                double qty = itemRequest.quantity() != null ? itemRequest.quantity() : 1.0;
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

                LineItem lineItem = LineItem.builder()
                        .description(itemRequest.description())
                        .unitPrice(price)
                        .quantity(qty)
                        .total(lineTotal)
                        .quote(quote)
                        .build();

                quote.getLineItems().add(lineItem);
                total = total.add(lineTotal);
            }
        }
        quote.setTotalAmount(total);

        // Save Mockups if provided
        if (request.mockupImageUrls() != null) {
            quote.setMockupImageUrls(request.mockupImageUrls());
        }

        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    // --- 2. ADMIN UPDATES QUOTE (Prices, Mockups) ---
    @Override
    @Transactional
    public QuoteDTO updateQuote(UUID quoteId, CreateQuoteRequest request) {
        Quote quote = getQuoteEntity(quoteId);

        // Security Check: Prevent editing finalized quotes
        if (quote.getStatus() == QuoteStatus.APPROVED ||
                quote.getStatus() == QuoteStatus.DEPOSIT_PAID ||
                quote.getStatus() == QuoteStatus.REJECTED) {
            throw new IllegalStateException("Cannot edit a finalized quote.");
        }

        quote.setTitle(request.title());

        // Only update requestDetails if provided (don't wipe it out)
        if (request.requestDetails() != null && !request.requestDetails().isEmpty()) {
            quote.setRequestDetails(request.requestDetails());
        }

        if (request.mockupImageUrls() != null) {
            quote.setMockupImageUrls(request.mockupImageUrls());
        }

        // Rebuild Line Items
        quote.getLineItems().clear();
        BigDecimal total = BigDecimal.ZERO;
        if (request.lineItems() != null) {
            for (var itemRequest : request.lineItems()) {
                BigDecimal price = itemRequest.unitPrice() != null ? itemRequest.unitPrice() : BigDecimal.ZERO;
                double qty = itemRequest.quantity() != null ? itemRequest.quantity() : 1.0;
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

                LineItem lineItem = LineItem.builder()
                        .description(itemRequest.description())
                        .unitPrice(price)
                        .quantity(qty)
                        .total(lineTotal)
                        .quote(quote)
                        .build();

                quote.getLineItems().add(lineItem);
                total = total.add(lineTotal);
            }
        }
        quote.setTotalAmount(total);

        // Recalculate deposit if estimate was already sent but price changed
        if (quote.getStatus() == QuoteStatus.ESTIMATE_SENT) {
            quote.setDepositAmount(total.multiply(new BigDecimal("0.5")));
        }

        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    // --- 3. ADMIN SENDS ESTIMATE (Status: ESTIMATE_SENT) ---
    @Override
    public QuoteDTO sendEstimateToCustomer(UUID quoteId) {
        Quote quote = getQuoteEntity(quoteId);

        // Generate Token & 50% Deposit Requirement
        String token = UUID.randomUUID().toString();
        quote.setMagicLinkToken(token);
        quote.setDepositAmount(quote.getTotalAmount().multiply(new BigDecimal("0.5")));
        quote.setStatus(QuoteStatus.ESTIMATE_SENT);
        quote.setEstimateSentAt(Instant.now());
        Quote saved = quoteRepository.save(quote);

        // NOTIFICATION LOGIC
        try {
            // 1. Get Customer Email/Phone
            var customer = customerClient.getCustomerById(quote.getCustomerId());

            // 2. Send Notification (Email/SMS)
            String magicLink = "http://localhost:4200/estimate/" + quoteId + "?token=" + token;

            NotificationRequest email = NotificationRequest.builder()
                    .customerId(quote.getCustomerId())
                    .channel("EMAIL")
                    .recipient(customer.email())
                    .templateKey("estimate-sent-email")
                    .parameters(Map.of(
                            "customerName", customer.firstName(),
                            "quoteTitle", quote.getTitle(),
                            "magicLink", magicLink,
                            "year", String.valueOf(Instant.now().atZone(ZoneId.systemDefault()).getYear())
                    ))
                    .build();

            notificationClient.sendNotification(email);


            NotificationRequest sms = NotificationRequest.builder()
                    .customerId(quote.getCustomerId())
                    .channel("SMS")
                    .recipient(customer.phoneNumber())
                    .templateKey("estimate-sent-sms")
                    .parameters(Map.of(
                            "customerName", customer.firstName(),
                            "quoteTitle", quote.getTitle(),
                            "magicLink", magicLink
                    ))
                    .build();

            notificationClient.sendNotification(sms);


        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
            // Don't fail the transaction, just log it
        }

        return jobMapper.mapToQuoteDTO(saved);
    }

    // --- 4. CUSTOMER APPROVES (Status: APPROVED) ---
    @Override
    public QuoteDTO customerApprove(UUID quoteId, String token) {
        Quote quote = getQuoteEntity(quoteId);
        validateToken(quote, token);

        quote.setStatus(QuoteStatus.APPROVED);
        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    // --- 5. CUSTOMER PAYS DEPOSIT (Status: DEPOSIT_PAID -> JOB CREATED) ---
    @Override
    @Transactional
    public QuoteDTO payDeposit(UUID quoteId, String token, BigDecimal amountPaid) {
        Quote quote = getQuoteEntity(quoteId);
        validateToken(quote, token);

        if (amountPaid.compareTo(quote.getDepositAmount()) < 0) {
            throw new IllegalArgumentException("Payment insufficient.");
        }

        quote.setStatus(QuoteStatus.DEPOSIT_PAID);
        quote.setApprovedAt(Instant.now());
        Quote savedQuote = quoteRepository.save(quote);

        // 1. Create Job
        Job job = createJobFromQuote(savedQuote);

        // 2. Trigger Schedule Service
        try {
            CreateScheduleRequest scheduleRequest = CreateScheduleRequest.builder()
                    .jobId(job.getId())
                    .customerId(quote.getCustomerId())
                    .propertyId(quote.getPropertyId())
                    .description("Job created from Quote: " + quote.getTitle())
                    .status("UNSCHEDULED")
                    .build();

            scheduleClient.createSchedule(scheduleRequest);
        } catch (Exception e) {
            System.err.println("Failed to create schedule: " + e.getMessage());
        }

        // 3. Send Receipt Notification
        // (Logic similar to sendEstimate, calling notificationClient)

        return jobMapper.mapToQuoteDTO(savedQuote);
    }

    // --- 6. REJECT (Status: REJECTED) ---
    @Override
    public QuoteDTO rejectQuote(UUID quoteId) {
        Quote quote = getQuoteEntity(quoteId);
        quote.setStatus(QuoteStatus.REJECTED);
        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    // --- ADMIN FORCE APPROVE (Optional) ---
    @Override
    @Transactional
    public QuoteDTO approveQuote(UUID quoteId) {
        Quote quote = getQuoteEntity(quoteId);
        quote.setStatus(QuoteStatus.APPROVED);
        quote.setApprovedAt(Instant.now());
        createJobFromQuote(quote);
        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    // --- READ OPERATIONS ---

    @Override
    public List<QuoteDTO> getAllQuotes() {
        return quoteRepository.findAll().stream()
                .map(jobMapper::mapToQuoteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuoteDTO getQuoteById(UUID quoteId) {
        return jobMapper.mapToQuoteDTO(getQuoteEntity(quoteId));
    }

    @Override
    public List<QuoteDTO> getQuotesForCustomer(UUID customerId) {
        return quoteRepository.findByCustomerId(customerId).stream()
                .map(jobMapper::mapToQuoteDTO)
                .collect(Collectors.toList());
    }

    // --- PRIVATE HELPERS ---

    private Quote getQuoteEntity(UUID id) {
        return quoteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quote not found with ID: " + id));
    }

    private void validateToken(Quote quote, String token) {
        if (quote.getMagicLinkToken() == null || !quote.getMagicLinkToken().equals(token)) {
            throw new IllegalArgumentException("Invalid or expired Magic Link Token");
        }
    }

    private Job createJobFromQuote(Quote quote) {
        Job job = Job.builder()
                .customerId(quote.getCustomerId())
                .propertyId(quote.getPropertyId())
                .quote(quote)
                .status(JobStatus.PENDING)
                .build();
        return jobRepository.save(job);
    }
}