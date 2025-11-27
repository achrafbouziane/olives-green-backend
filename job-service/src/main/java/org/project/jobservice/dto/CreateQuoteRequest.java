package org.project.jobservice.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record CreateQuoteRequest(
        UUID customerId,
        UUID propertyId,
        String title,
        String customerName,
        String customerEmail,
        String customerPhone,
        String serviceAddress,
        List<CreateLineItemRequest> lineItems,
        List<String> mockupImageUrls,
        BigDecimal discount
) {}