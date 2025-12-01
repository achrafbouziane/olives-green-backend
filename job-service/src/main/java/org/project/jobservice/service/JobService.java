package org.project.jobservice.service;

import org.project.jobservice.dto.*;
import org.project.jobservice.domain.JobStatus; // Or use String/Enum

import java.util.List;
import java.util.UUID;

public interface JobService {

    List<JobDTO> getAllJobs(UUID userId, String userRole);
    JobDTO getJobById(UUID jobId, UUID userId, String userRole);
    void scheduleJob(CreateScheduleRequest request, String userRole);
    JobDTO updateJobStatus(UUID jobId, JobStatus status, String userRole);
    JobVisitDTO checkIn(UUID jobId, UUID assignedEmployeeId);
    JobVisitDTO updateVisit(UUID visitId, JobVisitRequest request, UUID userId, String userRole);
    List<JobDTO> getJobsForEmployee(UUID assignedEmployeeId);

    List<JobDTO> getJobsForCustomer(UUID customerId);

    JobDTO assignEmployeeToJob(AssignEmployeeRequest request);
    JobDTO autoScheduleJob(UUID jobId);
    void renewJobSeries(UUID lastJobId);

}