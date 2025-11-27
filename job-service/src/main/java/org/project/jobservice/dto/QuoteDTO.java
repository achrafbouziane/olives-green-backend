package org.project.jobservice.dto;

import org.project.jobservice.domain.QuoteStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        String customerName,
        String customerEmail,
        String customerPhone,
        String serviceAddress,

        LocalDateTime createdAt,
        List<LineItemDTO> lineItems,
        List<String> mockupImageUrls,
        BigDecimal discount
) {}