package org.project.jobservice.service.impl;
import org.project.jobservice.dto.AssignEmployeeRequest;
import org.project.jobservice.dto.JobDTO;
import org.project.jobservice.dto.UpdateJobStatusRequest;
import org.project.jobservice.mapper.JobMapper;
import org.project.jobservice.entity.Job;
import org.project.jobservice.repository.JobRepository;
import org.project.jobservice.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    public JobDTO getJobById(UUID jobId) {
        Job job = findJobById(jobId);
        return jobMapper.mapToJobDTO(job);
    }

    @Override
    public List<JobDTO> getJobsForEmployee(UUID employeeId) {
        // This is a key feature for your employee's dashboard
        return jobRepository.findByAssignedEmployeeId(employeeId).stream()
                .map(jobMapper::mapToJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> getJobsForCustomer(UUID customerId) {
        // This is for your admin panel's customer view
        return jobRepository.findByCustomerId(customerId).stream()
                .map(jobMapper::mapToJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobDTO assignEmployeeToJob(AssignEmployeeRequest request) {
        Job job = findJobById(request.jobId());

        // In a real app, you'd call user-service to verify the employeeId exists

        job.setAssignedEmployeeId(request.employeeId());

        // When you assign an employee, the job is now "Scheduled"
        if (job.getStatus() == org.project.jobservice.domain.JobStatus.PENDING) {
            job.setStatus(org.project.jobservice.domain.JobStatus.SCHEDULED);
        }

        Job savedJob = jobRepository.save(job);
        return jobMapper.mapToJobDTO(savedJob);
    }

    @Override
    @Transactional
    public JobDTO updateJobStatus(UpdateJobStatusRequest request) {
        Job job = findJobById(request.jobId());

        // Add business logic here if needed (e.g., can't go from COMPLETED to PENDING)

        job.setStatus(request.newStatus());
        Job savedJob = jobRepository.save(job);
        return jobMapper.mapToJobDTO(savedJob);
    }

    // --- Private Helper Method ---

    private Job findJobById(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found with ID: " + jobId));
    }
}