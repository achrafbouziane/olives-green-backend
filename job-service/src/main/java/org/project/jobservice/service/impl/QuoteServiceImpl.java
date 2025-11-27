package org.project.jobservice.service.impl;

import org.project.jobservice.client.*;
import org.project.jobservice.client.CustomerClient;
import org.project.jobservice.domain.JobStatus;
import org.project.jobservice.domain.NotificationChannel;
import org.project.jobservice.domain.QuoteStatus;
import org.project.jobservice.dto.*;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

    // Feign Clients
    private final NotificationClient notificationClient;
    private final CustomerClient customerClient;
    private final InvoiceClient invoiceClient;

    @Override
    @Transactional
    public QuoteDTO createQuote(CreateQuoteRequest request) {
        BigDecimal discount = request.discount() != null ? request.discount() : BigDecimal.ZERO;

        Quote quote = Quote.builder()
                .customerId(request.customerId())
                .propertyId(request.propertyId())
                .title(request.title())
                .status(QuoteStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .lineItems(new ArrayList<>())
                .discount(discount)
                .totalAmount(BigDecimal.ZERO)

                // Snapshot Data
                .customerName(request.customerName())
                .customerEmail(request.customerEmail())
                .customerPhone(request.customerPhone())
                .serviceAddress(request.serviceAddress())

                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
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
                subtotal = subtotal.add(lineTotal);
            }
        }

        BigDecimal discountAmount = subtotal.multiply(discount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalTotal = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);
        quote.setTotalAmount(finalTotal);

        if (request.mockupImageUrls() != null) {
            quote.setMockupImageUrls(request.mockupImageUrls());
        }

        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    @Override
    @Transactional
    public QuoteDTO updateQuote(UUID quoteId, CreateQuoteRequest request) {
        Quote quote = getQuoteEntity(quoteId);

        if (quote.getStatus() == QuoteStatus.APPROVED ||
                quote.getStatus() == QuoteStatus.DEPOSIT_PAID ||
                quote.getStatus() == QuoteStatus.REJECTED) {
            throw new IllegalStateException("Cannot edit a finalized quote.");
        }

        quote.setTitle(request.title());
        if (request.mockupImageUrls() != null) quote.setMockupImageUrls(request.mockupImageUrls());

        BigDecimal discount = request.discount() != null ? request.discount() : BigDecimal.ZERO;
        quote.setDiscount(discount);

        quote.getLineItems().clear();
        BigDecimal subtotal = BigDecimal.ZERO;

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
                subtotal = subtotal.add(lineTotal);
            }
        }

        BigDecimal discountAmount = subtotal.multiply(discount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalTotal = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);
        quote.setTotalAmount(finalTotal);

        if (quote.getStatus() == QuoteStatus.ESTIMATE_SENT) {
            quote.setDepositAmount(finalTotal.multiply(new BigDecimal("0.5")));
        }

        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    @Override
    public QuoteDTO sendEstimateToCustomer(UUID quoteId) {
        Quote quote = getQuoteEntity(quoteId);
        String token = UUID.randomUUID().toString();
        quote.setMagicLinkToken(token);
        quote.setDepositAmount(quote.getTotalAmount().multiply(new BigDecimal("0.5")));
        quote.setStatus(QuoteStatus.ESTIMATE_SENT);
        quote.setEstimateSentAt(LocalDateTime.now());
        Quote saved = quoteRepository.save(quote);

        try {
            // Use Snapshot data if available, else fetch
            String emailTo = quote.getCustomerEmail();
            String nameTo = quote.getCustomerName();
            if(emailTo == null) {
                var customer = customerClient.getCustomerById(quote.getCustomerId());
                emailTo = customer.email();
                nameTo = customer.firstName();
            }

            String magicLink = "http://localhost:4200/estimate/" + quoteId + "?token=" + token;

            NotificationRequest email = NotificationRequest.builder()
                    .channel(NotificationChannel.EMAIL)
                    .recipient(emailTo)
                    .templateKey("estimate-sent-email")
                    .parameters(Map.of(
                            "customerName", nameTo,
                            "quoteTitle", quote.getTitle(),
                            "magicLink", magicLink,
                            "year", String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).getYear())
                    ))
                    .build();
            notificationClient.sendNotification(email);

        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return jobMapper.mapToQuoteDTO(saved);
    }

    @Override
    public QuoteDTO customerApprove(UUID quoteId, String token) {
        Quote quote = getQuoteEntity(quoteId);
        validateToken(quote, token);
        quote.setStatus(QuoteStatus.APPROVED);
        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    @Override
    @Transactional
    public QuoteDTO payDeposit(UUID quoteId, String token, BigDecimal amountPaid) {
        Quote quote = getQuoteEntity(quoteId);
        validateToken(quote, token);

        if (amountPaid.compareTo(quote.getDepositAmount()) < 0) {
            throw new IllegalArgumentException("Payment insufficient.");
        }

        // 1. Update Quote
        quote.setStatus(QuoteStatus.DEPOSIT_PAID);
        quote.setApprovedAt(LocalDateTime.now());
        Quote savedQuote = quoteRepository.save(quote);

        // 2. Create Job
        Job job = createJobFromQuote(savedQuote);

        // 3. Create Invoice Record
        try {
            // Map items
            var invoiceItems = savedQuote.getLineItems().stream()
                    .map(i -> new InvoiceLineItem(i.getDescription(), i.getQuantity(), i.getUnitPrice()))
                    .collect(Collectors.toList());

            CreateInvoiceRequest invoiceRequest = new CreateInvoiceRequest(
                    job.getId(),
                    savedQuote.getCustomerId(),
                    savedQuote.getCustomerName(),
                    savedQuote.getCustomerEmail(),
                    savedQuote.getCustomerPhone(),
                    savedQuote.getServiceAddress(),
                    savedQuote.getTotalAmount(),
                    amountPaid,
                    "DEPOSIT",
                    invoiceItems
            );

            invoiceClient.createInvoice(invoiceRequest);

            // 4. Send Receipt Notification
            NotificationRequest receipt = NotificationRequest.builder()
                    .channel(NotificationChannel.EMAIL)
                    .recipient(quote.getCustomerEmail())
                    .templateKey("deposit-receipt")
                    .parameters(Map.of(
                            "customerName", quote.getCustomerName(),
                            "quoteTitle", quote.getTitle(),
                            "amountPaid", amountPaid.toString(),
                            "date", LocalDateTime.now().toString().substring(0, 10)
                    ))
                    .build();
            notificationClient.sendNotification(receipt);

        } catch (Exception e) {
            System.err.println("Failed to create invoice record or send receipt: " + e.getMessage());
        }

        return jobMapper.mapToQuoteDTO(savedQuote);
    }

    @Override
    public QuoteDTO rejectQuote(UUID quoteId) {
        Quote quote = getQuoteEntity(quoteId);
        quote.setStatus(QuoteStatus.REJECTED);
        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    @Override
    @Transactional
    public QuoteDTO approveQuote(UUID quoteId) {
        Quote quote = getQuoteEntity(quoteId);
        quote.setStatus(QuoteStatus.APPROVED);
        quote.setApprovedAt(LocalDateTime.now());
        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    @Override
    @Transactional
    public QuoteDTO updateQuoteStatus(UUID quoteId, QuoteStatus status) {
        Quote quote = getQuoteEntity(quoteId);

        // RESET Logic
        if (status == QuoteStatus.REQUESTED || status == QuoteStatus.REJECTED) {

            // 1. Fetch ALL jobs linked to this quote (Returns List<Job>)
            List<Job> jobs = jobRepository.findByQuoteId(quoteId);

            if (!jobs.isEmpty()) {
                // 2. Safety Check: If ANY job in the series is already done, block the reset.
                boolean hasStartedJobs = jobs.stream()
                        .anyMatch(j -> j.getStatus() == JobStatus.COMPLETED || j.getStatus() == JobStatus.INVOICED);

                if (hasStartedJobs) {
                    throw new IllegalStateException("Cannot reset quote: Work has already started on one or more jobs.");
                }

                // 3. Delete ALL pending jobs (Original + Recurring clones)
                jobRepository.deleteAll(jobs);
            }
        }
        // MANUAL DEPOSIT Logic
        else if (status == QuoteStatus.DEPOSIT_PAID) {
            quote.setApprovedAt(LocalDateTime.now());
            if (jobRepository.findByQuoteId(quoteId).isEmpty()) {
                createJobFromQuote(quote);
            }
        }
        else if (status == QuoteStatus.APPROVED) {
            quote.setApprovedAt(LocalDateTime.now());
        }

        quote.setStatus(status);
        return jobMapper.mapToQuoteDTO(quoteRepository.save(quote));
    }

    // Read Ops
    @Override
    public List<QuoteDTO> getAllQuotes() { return quoteRepository.findAll().stream().map(jobMapper::mapToQuoteDTO).collect(Collectors.toList()); }
    @Override
    public QuoteDTO getQuoteById(UUID quoteId) { return jobMapper.mapToQuoteDTO(getQuoteEntity(quoteId)); }
    @Override
    public List<QuoteDTO> getQuotesForCustomer(UUID customerId) { return quoteRepository.findByCustomerId(customerId).stream().map(jobMapper::mapToQuoteDTO).collect(Collectors.toList()); }

    // Helpers
    private Quote getQuoteEntity(UUID id) { return quoteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Quote not found: " + id)); }
    private void validateToken(Quote quote, String token) { if (quote.getMagicLinkToken() == null || !quote.getMagicLinkToken().equals(token)) throw new IllegalArgumentException("Invalid token"); }
    private Job createJobFromQuote(Quote quote) {
        Job job = Job.builder()
                .customerId(quote.getCustomerId())
                .propertyId(quote.getPropertyId())
                .quote(quote)
                .title(quote.getTitle())
                // Copy Snapshot Data
                .customerName(quote.getCustomerName())
                .customerEmail(quote.getCustomerEmail())
                .customerPhone(quote.getCustomerPhone())
                .serviceAddress(quote.getServiceAddress())
                .status(JobStatus.PENDING)
                .build();
        return jobRepository.save(job);
    }
}