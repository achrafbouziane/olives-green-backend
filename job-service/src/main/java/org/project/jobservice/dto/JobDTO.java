package org.project.jobservice.dto;

import org.project.jobservice.domain.JobStatus;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.project.jobservice.domain.JobFrequency;

@Builder(toBuilder = true)
public record JobDTO(
        UUID id,
        UUID assignedEmployeeId,
        UUID customerId,
        UUID propertyId,
        UUID quoteId, // The quote this job came from
        String title,
        JobStatus status,
        LocalDateTime scheduledDate,
        String customerName,
        String customerEmail,
        String customerPhone,
        String serviceAddress,
        List<JobVisitDTO> visits,
        JobFrequency frequency,
        UUID recurringGroupId
) {}
