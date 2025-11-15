package org.project.jobservice.domain;

public enum JobStatus {
    PENDING,    // Just created, not scheduled
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    INVOICED
}