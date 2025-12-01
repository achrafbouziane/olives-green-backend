package org.project.jobservice.dto;

import lombok.Builder;
import org.project.jobservice.domain.NotificationChannel;

import java.util.Map;

@Builder(toBuilder = true)
public record NotificationRequest(
        NotificationChannel channel,
        String recipient,
        String templateKey,
        Map<String, Object> parameters
) {}