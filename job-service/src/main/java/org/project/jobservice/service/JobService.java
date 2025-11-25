package org.project.jobservice.service;

import org.project.jobservice.dto.AssignEmployeeRequest;
import org.project.jobservice.dto.JobDTO;
import org.project.jobservice.dto.UpdateJobStatusRequest;

import java.util.List;
import java.util.UUID;

public interface JobService {

    JobDTO getJobById(UUID jobId);

    List<JobDTO> getJobsForEmployee(UUID employeeId);

    List<JobDTO> getJobsForCustomer(UUID customerId);

    JobDTO assignEmployeeToJob(AssignEmployeeRequest request);

    JobDTO updateJobStatus(UpdateJobStatusRequest request);
}