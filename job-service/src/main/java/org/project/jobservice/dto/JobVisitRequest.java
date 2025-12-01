package org.project.jobservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class JobVisitRequest {
    private UUID assignedEmployeeId; // ✅ Renamed
    private String notes;
    private List<String> tasks;
    private List<String> beforePhotos;
    private List<String> afterPhotos;
    private LocalDateTime endTime;
}