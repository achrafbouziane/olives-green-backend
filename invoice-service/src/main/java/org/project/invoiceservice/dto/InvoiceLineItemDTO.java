package org.project.invoiceservice.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record InvoiceLineItemDTO(
        UUID id,
        String description,
        BigDecimal unitPrice,
        Double quantity,
        BigDecimal total
) {}