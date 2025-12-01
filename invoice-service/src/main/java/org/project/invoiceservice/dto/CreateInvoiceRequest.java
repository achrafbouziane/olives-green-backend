package org.project.invoiceservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record CreateInvoiceRequest(
        UUID customerId,
        UUID jobId,
        String customerName,
        String customerEmail,
        String customerPhone,
        String serviceAddress,
        String type,
        @JsonProperty("items")
        List<CreateInvoiceLineItem> lineItems
) {}