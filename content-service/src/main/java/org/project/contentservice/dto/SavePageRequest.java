package org.project.contentservice.dto;

public record SavePageRequest(
        String pageSlug,
        String title,
        String subTitle,
        String imageUrl,
        String htmlContent
) {}
