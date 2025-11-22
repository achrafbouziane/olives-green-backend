package org.project.jobservice.dto;

import org.project.jobservice.domain.JobStatus;
import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
public record JobDTO(
        UUID id,
        UUID assignedEmployeeId,
        UUID customerId,
        UUID propertyId,
        UUID quoteId, // The quote this job came from
        JobStatus status,
        LocalDate scheduledDate,
        String clientName,
        String propertyAdress
) {}
