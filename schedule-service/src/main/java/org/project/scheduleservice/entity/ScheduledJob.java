package org.project.scheduleservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduled_jobs")
public class ScheduledJob {

    @Id
    @GeneratedValue
    private UUID id;

    // This is the ID from your job-service
    @Column(nullable = false, unique = true) // A job can only be scheduled once
    private UUID jobId;

    // This is the ID from your user-service
    @Column(nullable = false)
    private UUID employeeId;

    // These are the "When" details
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String notes; // e.g., "Bring the small mower"
}