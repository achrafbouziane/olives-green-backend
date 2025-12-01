package org.project.jobservice.dto;

import java.util.UUID;

public record AssignEmployeeRequest(
        UUID jobId,
        UUID assignedEmployeeId
) {}