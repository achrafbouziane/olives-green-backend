package org.project.jobservice.dto;


import org.project.jobservice.domain.JobStatus;
import java.util.UUID;

public record UpdateJobStatusRequest(
        UUID jobId,
        JobStatus newStatus
) {}