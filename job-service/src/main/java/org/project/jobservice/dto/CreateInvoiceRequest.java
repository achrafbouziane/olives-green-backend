package org.project.jobservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Minimal DTO for the request
@Builder
public record CreateInvoiceRequest(
        UUID jobId,
        UUID customerId,
        String customerName,
        String customerEmail,
        String customerPhone,
        String serviceAddress,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String type, // "DEPOSIT" or "FINAL"
        List<InvoiceLineItem> items
) {
}
