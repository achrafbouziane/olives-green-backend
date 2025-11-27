package org.project.scheduleservice.repository;

import org.project.scheduleservice.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, UUID> {

    // Find all jobs for one employee (for their personal calendar)
    List<ScheduledJob> findByAssignedEmployeeId(UUID assignedEmployeeId);

    // Find all jobs for an employee within a specific time range
    List<ScheduledJob> findByAssignedEmployeeIdAndStartTimeBetween(UUID assignedEmployeeId, LocalDateTime start, LocalDateTime end);

    // Find all jobs in a time range (for the admin's master calendar)
    List<ScheduledJob> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find a schedule by the job ID
    ScheduledJob findByJobId(UUID jobId);

    @Query("""
    SELECT s FROM ScheduledJob s
    WHERE s.assignedEmployeeId = :employeeId
      AND s.startTime < :end
      AND s.endTime > :start
    """)
    List<ScheduledJob> findConflictingSchedules(UUID employeeId, LocalDateTime start, LocalDateTime end);

}