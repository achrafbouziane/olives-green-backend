package org.project.invoiceservice.dto;


import org.project.invoiceservice.domain.InvoiceStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record InvoiceDTO(
        UUID id,
        UUID customerId,
        UUID jobId,
        InvoiceStatus status,
        BigDecimal totalAmount,
        Instant issuedDate,
        LocalDate dueDate,
        Instant paidDate,
        List<InvoiceLineItemDTO> lineItems
) {}