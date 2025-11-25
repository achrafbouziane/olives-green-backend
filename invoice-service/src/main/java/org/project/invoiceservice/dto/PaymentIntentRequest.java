package org.project.invoiceservice.dto;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentIntentRequest(
        UUID quoteId,
        BigDecimal amount, // The base deposit amount
        String currency
) {}