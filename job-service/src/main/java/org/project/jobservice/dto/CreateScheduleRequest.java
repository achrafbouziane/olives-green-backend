package org.project.jobservice.dto;

import lombok.Builder;
import java.util.UUID;

@Builder(toBuilder = true)
public record CreateScheduleRequest(
        UUID jobId,
        UUID customerId,
        UUID propertyId,
        String description,
        String status // "UNSCHEDULED"
) {}
