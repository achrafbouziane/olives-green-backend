package org.project.contentservice.dto;

import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
public record ServicePageDTO(
        UUID id,
        String pageSlug,
        String title,
        String subTitle,
        String imageUrl,
        String description,   // Replaces htmlContent
        List<String> features // New structured list
) {}