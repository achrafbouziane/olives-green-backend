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
        List<CreateLineItemRequest> lineItems
) {}