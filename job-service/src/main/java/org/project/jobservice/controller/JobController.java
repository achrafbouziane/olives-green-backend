package org.project.jobservice.controller;

import jakarta.validation.Valid;
import org.project.jobservice.dto.*;
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

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        // If role headers are missing (e.g. local test), default to safe/restricted view or error
        if (userRole == null) userRole = "EMPLOYEE";
        return ResponseEntity.ok(jobService.getAllJobs(userId, userRole));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        return ResponseEntity.ok(jobService.getJobById(id, userId, userRole));
    }

    // --- WRITE OPERATIONS (Admin Only) ---

    @PostMapping("/{id}/schedule")
    public ResponseEntity<Void> scheduleJob(
            @PathVariable UUID id,
            @RequestBody @Valid CreateScheduleRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        // Ensure PathVariable ID matches Request Body ID for safety
        CreateScheduleRequest secureRequest = request.toBuilder().jobId(id).build();
        jobService.scheduleJob(secureRequest, userRole);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateJobStatusRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        return ResponseEntity.ok(jobService.updateJobStatus(id, request.newStatus(), userRole));
    }

    // --- EMPLOYEE OPERATIONS (Check-In / Logs) ---

    @PostMapping("/{id}/checkin")
    public ResponseEntity<JobVisitDTO> checkIn(
            @PathVariable UUID id,
            @RequestParam UUID assignedEmployeeId,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        // Security: Ensure the user checking in is actually the user logged in (unless Admin)
        if (!"ADMIN".equals(userRole) && !assignedEmployeeId.equals(userId)) {
            throw new SecurityException("You cannot check in for another employee.");
        }

        return ResponseEntity.ok(jobService.checkIn(id, assignedEmployeeId));
    }

    @PutMapping("/visits/{visitId}")
    public ResponseEntity<JobVisitDTO> updateVisit(
            @PathVariable UUID visitId,
            @RequestBody @Valid JobVisitRequest request,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        return ResponseEntity.ok(jobService.updateVisit(visitId, request, userId, userRole));
    }
    @GetMapping("/employee/{assignedEmployeeId}")
    public ResponseEntity<List<JobDTO>> getJobsForEmployee(@PathVariable UUID assignedEmployeeId) {
        return ResponseEntity.ok(jobService.getJobsForEmployee(assignedEmployeeId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<JobDTO>> getJobsForCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(jobService.getJobsForCustomer(customerId));
    }

    @PatchMapping("/assign")
    public ResponseEntity<JobDTO> assignEmployee(@RequestBody AssignEmployeeRequest request) {
        return ResponseEntity.ok(jobService.assignEmployeeToJob(request));
    }
    @PostMapping("/{id}/auto-schedule")
    public ResponseEntity<JobDTO> autoScheduleJob(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        // 1. Security Check (Optional: Allow employees to auto-schedule their own jobs?)
        // For now, let's allow ADMIN or the assigned EMPLOYEE
        return ResponseEntity.ok(jobService.autoScheduleJob(id));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<Void> renewSeries(@PathVariable UUID id) {
        jobService.renewJobSeries(id);
        return ResponseEntity.ok().build();
    }


}