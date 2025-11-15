package org.project.jobservice.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record LineItemDTO(
        UUID id,
        String description,
        BigDecimal unitPrice,
        Double quantity,
        BigDecimal total
) {}