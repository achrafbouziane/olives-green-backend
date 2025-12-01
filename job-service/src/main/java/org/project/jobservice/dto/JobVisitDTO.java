package org.project.jobservice.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record JobVisitDTO(
        UUID id,
        UUID jobId,
        UUID assignedEmployeeId, // ✅ Renamed
        LocalDateTime checkInTime,
        LocalDateTime checkOutTime,
        String notes,
        List<String> tasksCompleted,
        List<String> beforePhotoUrls,
        List<String> afterPhotoUrls
) {}