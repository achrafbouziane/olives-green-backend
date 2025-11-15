package org.project.jobservice.repository;

import org.project.jobservice.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByAssignedEmployeeId(UUID employeeId);
    List<Job> findByCustomerId(UUID customerId);
}