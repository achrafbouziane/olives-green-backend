package org.project.invoiceservice.dto;


import org.project.invoiceservice.domain.InvoiceStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record InvoiceDTO(
        UUID id,
        UUID customerId,
        UUID jobId,
        InvoiceStatus status,
        BigDecimal totalAmount,
        LocalDateTime issuedDate,
        LocalDateTime dueDate,
        LocalDateTime paidDate,
        String customerName,
        String customerEmail,
        String customerPhone,
        String serviceAddress,
        List<InvoiceLineItemDTO> lineItems
) {}