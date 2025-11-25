package org.project.jobservice.dto;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record NotificationRequest(
        UUID customerId,
        String type, // "ESTIMATE_SENT" or "PAYMENT_RECEIVED"
        String channel, // "EMAIL" or "SMS"
        String recipient, // Email address or Phone number
        String subject,
        String message,
        String templateKey,
        Map<String, Object> parameters
) {}