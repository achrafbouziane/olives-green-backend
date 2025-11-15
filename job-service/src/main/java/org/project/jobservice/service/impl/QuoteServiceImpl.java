package org.project.jobservice.service.impl;

import org.project.jobservice.dto.CreateQuoteRequest;
import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.mapper.JobMapper;
import org.project.jobservice.entity.*;
import org.project.jobservice.domain.*;
import org.project.jobservice.repository.JobRepository;
import org.project.jobservice.repository.QuoteRepository;
import org.project.jobservice.service.QuoteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final JobRepository jobRepository; // For approving a quote
    private final JobMapper jobMapper;

    @Override
    @Transactional // Ensures this all saves or none of it does
    public QuoteDTO createQuote(CreateQuoteRequest request) {

        // 1. Build the main Quote object
        Quote quote = Quote.builder()
                .customerId(request.customerId())
                .propertyId(request.propertyId())
                .title(request.title())
                .status(QuoteStatus.DRAFT)
                .createdAt(Instant.now())
                .build();

        // 2. Build the LineItems and calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (var itemRequest : request.lineItems()) {
            BigDecimal lineTotal = itemRequest.unitPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));

            LineItem lineItem = LineItem.builder()
                    .description(itemRequest.description())
                    .unitPrice(itemRequest.unitPrice())
                    .quantity(itemRequest.quantity())
                    .total(lineTotal)
                    .quote(quote) // Link back to the parent quote
                    .build();

            quote.getLineItems().add(lineItem);
            total = total.add(lineTotal);
        }

        quote.setTotalAmount(total);

        // 3. Save the Quote (LineItems will be saved automatically due to Cascade)
        Quote savedQuote = quoteRepository.save(quote);

        return jobMapper.mapToQuoteDTO(savedQuote);
    }

    @Override
    public QuoteDTO getQuoteById(UUID quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new EntityNotFoundException("Quote not found"));
        return jobMapper.mapToQuoteDTO(quote);
    }

    @Override
    public List<QuoteDTO> getQuotesForCustomer(UUID customerId) {
        return quoteRepository.findByCustomerId(customerId).stream()
                .map(jobMapper::mapToQuoteDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuoteDTO approveQuote(UUID quoteId) {
        // 1. Find the quote
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new EntityNotFoundException("Quote not found"));

        // 2. Update its status
        quote.setStatus(QuoteStatus.APPROVED);
        quote.setApprovedAt(Instant.now());
        Quote savedQuote = quoteRepository.save(quote);

        // 3. **CRITICAL STEP: Create the Job**
        Job job = Job.builder()
                .customerId(quote.getCustomerId())
                .propertyId(quote.getPropertyId())
                .quote(savedQuote) // Link the job to the quote
                .status(JobStatus.PENDING) // New jobs are pending
                .build();

        jobRepository.save(job);

        return jobMapper.mapToQuoteDTO(savedQuote);
    }
}