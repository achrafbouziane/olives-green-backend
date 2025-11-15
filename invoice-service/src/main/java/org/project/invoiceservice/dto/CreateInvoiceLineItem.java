package org.project.invoiceservice.dto;

import java.math.BigDecimal;

public record CreateInvoiceLineItem(
        String description,
        BigDecimal unitPrice,
        Double quantity
) {}