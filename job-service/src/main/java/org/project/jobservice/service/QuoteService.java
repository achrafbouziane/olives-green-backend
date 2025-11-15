package org.project.jobservice.service;

import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.dto.CreateQuoteRequest;
import java.util.List;
import java.util.UUID;

public interface QuoteService {
    QuoteDTO createQuote(CreateQuoteRequest request);
    QuoteDTO getQuoteById(UUID quoteId);
    List<QuoteDTO> getQuotesForCustomer(UUID customerId);
    QuoteDTO approveQuote(UUID quoteId);
}