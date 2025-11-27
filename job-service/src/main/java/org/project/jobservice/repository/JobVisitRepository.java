package org.project.jobservice.repository;

import org.project.jobservice.entity.JobVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobVisitRepository extends JpaRepository<JobVisit, UUID> {
}