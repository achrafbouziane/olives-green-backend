package org.project.invoiceservice.dto;
import java.math.BigDecimal;

public record PaymentIntentResponse(
        String clientSecret,
        String id,
        BigDecimal amount,     // Total charged
        BigDecimal feeAmount   // The extra fee
) {}