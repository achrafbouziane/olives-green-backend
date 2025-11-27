package org.project.jobservice.dto;

import org.project.jobservice.domain.QuoteStatus;

public record UpdateQuoteStatusRequest(
        QuoteStatus status
) {}