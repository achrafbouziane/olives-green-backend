package org.project.scheduleservice.repository;

import org.project.scheduleservice.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, UUID> {

    // Find all jobs for one employee (for their personal calendar)
    List<ScheduledJob> findByEmployeeId(UUID employeeId);

    // Find all jobs for an employee within a specific time range
    List<ScheduledJob> findByEmployeeIdAndStartTimeBetween(UUID employeeId, LocalDateTime start, LocalDateTime end);

    // Find all jobs in a time range (for the admin's master calendar)
    List<ScheduledJob> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find a schedule by the job ID
    ScheduledJob findByJobId(UUID jobId);
}