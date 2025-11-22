package org.project.jobservice.service.impl;

import org.project.jobservice.client.CustomerClient;
import org.project.jobservice.client.ScheduleClient;
import org.project.jobservice.dto.*;
import org.project.jobservice.entity.Job;
import org.project.jobservice.domain.JobStatus;
import org.project.jobservice.mapper.JobMapper;
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
    private final CustomerClient customerClient; //
    private final ScheduleClient scheduleClient; //

    @Override
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::enrichJobDTO) // Use helper to add Client Name
                .collect(Collectors.toList());
    }

    @Override
    public JobDTO getJobById(UUID jobId) {
        Job job = findJobById(jobId);
        return enrichJobDTO(job);
    }

    @Override
    @Transactional
    public void scheduleJob(CreateScheduleRequest request) {
        // 1. Verify Job Exists
        Job job = findJobById(request.jobId());

        // 2. Call Schedule Service via Feign Client
        scheduleClient.createSchedule(request);

        // 3. Update Local Status to SCHEDULED
        job.setStatus(JobStatus.SCHEDULED);
        jobRepository.save(job);
    }

    @Override
    public List<JobDTO> getJobsForEmployee(UUID employeeId) {
        return jobRepository.findByAssignedEmployeeId(employeeId).stream()
                .map(this::enrichJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> getJobsForCustomer(UUID customerId) {
        return jobRepository.findByCustomerId(customerId).stream()
                .map(this::enrichJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobDTO assignEmployeeToJob(AssignEmployeeRequest request) {
        Job job = findJobById(request.jobId());

        job.setAssignedEmployeeId(request.employeeId());

        // Auto-update status to SCHEDULED if it was pending
        if (job.getStatus() == JobStatus.PENDING) {
            job.setStatus(JobStatus.SCHEDULED);
        }

        Job savedJob = jobRepository.save(job);
        return enrichJobDTO(savedJob);
    }

    @Override
    @Transactional
    public JobDTO updateJobStatus(UUID jobId, JobStatus status) {
        Job job = findJobById(jobId);
        job.setStatus(status);
        Job savedJob = jobRepository.save(job);
        return enrichJobDTO(savedJob);
    }

    // --- Helper Methods ---

    private Job findJobById(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job not found with ID: " + id));
    }

    /**
     * Converts Entity to DTO and fetches the Customer Name from Customer Service.
     */
    private JobDTO enrichJobDTO(Job job) {
        // 1. Map Entity to Record (clientName is null)
        JobDTO dto = jobMapper.mapToJobDTO(job);

        String clientName = "Unknown Customer";

        // 2. Fetch Customer Name
        try {
            if (job.getCustomerId() != null) {
                CustomerDTO customer = customerClient.getCustomerById(job.getCustomerId());
                if (customer != null) {
                    clientName = customer.firstName() + " " + customer.lastName();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch customer: " + e.getMessage());
        }

        return dto.toBuilder()
                .clientName(clientName)
                .build();
    }
}