package org.project.jobservice.dto;

import lombok.Builder;
import org.project.jobservice.domain.JobFrequency;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record CreateScheduleRequest(
        UUID jobId,
        UUID assignedEmployeeId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String notes,
        JobFrequency frequency,
        LocalDateTime recurrenceEndDate
) {}
