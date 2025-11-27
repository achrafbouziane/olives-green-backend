package org.project.jobservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InvoiceLineItem(String description, Double quantity, BigDecimal unitPrice) {
}
