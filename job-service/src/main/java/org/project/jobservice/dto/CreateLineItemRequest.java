package org.project.jobservice.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record CreateLineItemRequest(
        String description,
        BigDecimal unitPrice,
        Double quantity
) {}