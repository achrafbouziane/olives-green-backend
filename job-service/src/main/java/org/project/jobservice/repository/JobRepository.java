package org.project.jobservice.repository;

import org.project.jobservice.domain.JobStatus;
import org.project.jobservice.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByAssignedEmployeeId(UUID employeeId);
    List<Job> findByCustomerId(UUID customerId);
    List<Job> findByQuoteId(UUID quoteId);
    @Query("SELECT j FROM Job j WHERE j.status IN ('SCHEDULED', 'IN_PROGRESS') " +
            "AND j.scheduledDate >= :start AND j.scheduledDate < :end")
    List<Job> findActiveJobsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    long countByStatus(JobStatus status);
}