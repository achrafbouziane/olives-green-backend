package org.project.jobservice.dto;

import org.project.jobservice.domain.QuoteStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record QuoteDTO(
        UUID id,
        UUID customerId,
        UUID propertyId,
        String title,
        QuoteStatus status,
        BigDecimal totalAmount,

        // --- NEW FIELDS FOR ESTIMATE ---
        BigDecimal depositAmount,
        String magicLinkToken,
        // -------------------------------
        String requestDetails,

        Instant createdAt,
        List<LineItemDTO> lineItems,
        List<String> mockupImageUrls
) {}