package org.project.scheduleservice.service;

import org.project.scheduleservice.dto.CreateScheduleRequest;
import org.project.scheduleservice.dto.ScheduledJobDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleService {

    /**
     * Creates a new scheduled job entry.
     */
    ScheduledJobDTO createScheduledJob(CreateScheduleRequest request);

    /**
     * Gets the full schedule for a specific employee.
     */
    List<ScheduledJobDTO> getScheduleForEmployee(UUID assignedEmployeeId);

    /**
     * Gets the schedule for an employee for a specific day.
     */
    List<ScheduledJobDTO> getScheduleForEmployeeByDay(UUID assignedEmployeeId, LocalDateTime day);

    /**
     * Gets the master schedule for all employees for a specific day.
     */
    List<ScheduledJobDTO> getMasterScheduleByDay(LocalDateTime day);

    /**
     * Finds a scheduled job by its original jobId from job-service.
     */
    ScheduledJobDTO getScheduleByJobId(UUID jobId);

    /**
     * Deletes a scheduled job (e.g., "unschedule").
     */
    void deleteScheduledJob(UUID scheduleId);
}