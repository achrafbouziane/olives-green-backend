package org.project.scheduleservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateScheduleRequest(
        UUID jobId,
        UUID employeeId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String notes
) {}