package org.project.jobservice.service;

import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.dto.CreateQuoteRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface QuoteService {
    // Core CRUD
    QuoteDTO createQuote(CreateQuoteRequest request);
    List<QuoteDTO> getAllQuotes();
    QuoteDTO getQuoteById(UUID quoteId);
    List<QuoteDTO> getQuotesForCustomer(UUID customerId);

    // New "Estimate & Deposit" Workflow
    QuoteDTO sendEstimateToCustomer(UUID quoteId);
    QuoteDTO customerApprove(UUID quoteId, String token);
    QuoteDTO payDeposit(UUID quoteId, String token, BigDecimal amountPaid);
    QuoteDTO rejectQuote(UUID quoteId);

    // Admin Override (optional)
    QuoteDTO approveQuote(UUID quoteId);
    QuoteDTO updateQuote(UUID quoteId, CreateQuoteRequest request);
}