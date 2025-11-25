package org.project.invoiceservice.dto;


import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record CreateInvoiceRequest(
        UUID customerId,
        UUID jobId,
        // The job-service passes all the line items from its quote
        List<CreateInvoiceLineItem> lineItems
) {}