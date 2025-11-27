package org.project.scheduleservice.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ScheduledJobDTO(
        UUID id,
        UUID jobId,
        UUID assignedEmployeeId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String notes
) {}