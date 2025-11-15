package org.project.jobservice.controller;

import org.project.jobservice.dto.AssignEmployeeRequest;
import org.project.jobservice.dto.JobDTO;
import org.project.jobservice.dto.UpdateJobStatusRequest;
import org.project.jobservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // Get all jobs for a specific employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<JobDTO>> getJobsForEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(jobService.getJobsForEmployee(employeeId));
    }

    // Get all jobs for a specific customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<JobDTO>> getJobsForCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(jobService.getJobsForCustomer(customerId));
    }

    // Assign an employee to a job
    @PatchMapping("/assign")
    public ResponseEntity<JobDTO> assignEmployee(@RequestBody AssignEmployeeRequest request) {
        return ResponseEntity.ok(jobService.assignEmployeeToJob(request));
    }

    // Update a job's status
    @PatchMapping("/status")
    public ResponseEntity<JobDTO> updateStatus(@RequestBody UpdateJobStatusRequest request) {
        return ResponseEntity.ok(jobService.updateJobStatus(request));
    }
}