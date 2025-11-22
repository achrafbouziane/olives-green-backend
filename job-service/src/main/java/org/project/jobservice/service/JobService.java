package org.project.jobservice.service;

import org.project.jobservice.dto.AssignEmployeeRequest;
import org.project.jobservice.dto.JobDTO;
import org.project.jobservice.dto.CreateScheduleRequest;
import org.project.jobservice.domain.JobStatus; // Or use String/Enum

import java.util.List;
import java.util.UUID;

public interface JobService {

    List<JobDTO> getAllJobs(); // <--- NEW

    JobDTO getJobById(UUID jobId);

    void scheduleJob(CreateScheduleRequest request); // <--- NEW

    // Updated signature to accept ID directly from controller path
    JobDTO updateJobStatus(UUID jobId, JobStatus status);

    List<JobDTO> getJobsForEmployee(UUID employeeId);

    List<JobDTO> getJobsForCustomer(UUID customerId);

    JobDTO assignEmployeeToJob(AssignEmployeeRequest request);
}