package org.project.contentservice.dto;

import java.util.List;

public record SavePageRequest(
        String pageSlug,
        String title,
        String subTitle,
        String imageUrl,
        String description,   // Replaces htmlContent
        List<String> features // New structured list
) {}