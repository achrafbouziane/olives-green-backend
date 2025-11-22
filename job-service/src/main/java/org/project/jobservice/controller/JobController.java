package org.project.jobservice.controller;

import org.project.jobservice.dto.AssignEmployeeRequest;
import org.project.jobservice.dto.JobDTO;
import org.project.jobservice.dto.UpdateJobStatusRequest;
import org.project.jobservice.dto.CreateScheduleRequest; // Ensure DTO exists or reuse shared
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

    // ✅ 1. Get All Jobs (Matches frontend: useJobs)
    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // ✅ 2. Get Job By ID (Matches frontend: useJobById)
    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // ✅ 3. Schedule a Job (Matches frontend: scheduleJob)
    // Frontend calls: POST /api/v1/jobs/{id}/schedule
    @PostMapping("/{id}/schedule")
    public ResponseEntity<Void> scheduleJob(
            @PathVariable UUID id,
            @RequestBody CreateScheduleRequest request
    ) {
        // FIX: Rebuild immutable record with correct jobId
        CreateScheduleRequest fixedRequest = request.toBuilder()
                .jobId(id)
                .build();

        jobService.scheduleJob(fixedRequest);

        return ResponseEntity.ok().build();
    }


    // ✅ 4. Update Status (Matches frontend: updateStatus)
    // Frontend calls: PUT /api/v1/jobs/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<JobDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateJobStatusRequest request
    ) {
        // Use the ID from the path
        return ResponseEntity.ok(jobService.updateJobStatus(id, request.newStatus()));
    }

    // --- Existing Helper Endpoints ---

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<JobDTO>> getJobsForEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(jobService.getJobsForEmployee(employeeId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<JobDTO>> getJobsForCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(jobService.getJobsForCustomer(customerId));
    }

    @PatchMapping("/assign")
    public ResponseEntity<JobDTO> assignEmployee(@RequestBody AssignEmployeeRequest request) {
        return ResponseEntity.ok(jobService.assignEmployeeToJob(request));
    }
}