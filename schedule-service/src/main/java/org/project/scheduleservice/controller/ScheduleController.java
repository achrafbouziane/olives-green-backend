package org.project.scheduleservice.controller;

import org.project.scheduleservice.dto.CreateScheduleRequest;
import org.project.scheduleservice.dto.ScheduledJobDTO;
import org.project.scheduleservice.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduledJobDTO> scheduleJob(@RequestBody CreateScheduleRequest request) {
        ScheduledJobDTO scheduledJob = scheduleService.createScheduledJob(request);
        return new ResponseEntity<>(scheduledJob, HttpStatus.CREATED);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> unscheduleJob(@PathVariable UUID scheduleId) {
        scheduleService.deleteScheduledJob(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ScheduledJobDTO> getScheduleByJobId(@PathVariable UUID jobId) {
        return ResponseEntity.ok(scheduleService.getScheduleByJobId(jobId));
    }

    @GetMapping("/employee/{employeeId}/day")
    public ResponseEntity<List<ScheduledJobDTO>> getEmployeeScheduleForDay(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getScheduleForEmployeeByDay(employeeId, date));
    }

    @GetMapping("/day")
    public ResponseEntity<List<ScheduledJobDTO>> getMasterScheduleForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getMasterScheduleByDay(date));
    }
}