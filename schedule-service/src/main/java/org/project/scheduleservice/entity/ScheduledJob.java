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
    @Column(name = "job_id", nullable = false, unique = true)
    private UUID jobId;

    // This is the ID from your user-service
    @Column(name = "assigned_employee_id", nullable = false)
    private UUID assignedEmployeeId;


    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String notes; // e.g., "Bring the small mower"
}