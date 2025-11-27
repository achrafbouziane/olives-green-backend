package org.project.scheduleservice.service.impl;

import org.project.scheduleservice.dto.CreateScheduleRequest;
import org.project.scheduleservice.dto.ScheduledJobDTO;
import org.project.scheduleservice.mapper.ScheduleMapper;
import org.project.scheduleservice.entity.ScheduledJob;
import org.project.scheduleservice.repository.ScheduledJobRepository;
import org.project.scheduleservice.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduledJobRepository scheduledJobRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public ScheduledJobDTO createScheduledJob(CreateScheduleRequest request) {

        // FIX: Check if schedule already exists for this Job ID
        ScheduledJob existingJob = scheduledJobRepository.findByJobId(request.jobId());

        ScheduledJob jobToSave;

        if (existingJob != null) {
            // UPDATE existing record
            jobToSave = existingJob;
            jobToSave.setAssignedEmployeeId(request.assignedEmployeeId());
            jobToSave.setStartTime(request.startTime());
            jobToSave.setEndTime(request.endTime());
            jobToSave.setNotes(request.notes());
        } else {
            // CREATE new record
            jobToSave = ScheduledJob.builder()
                    .jobId(request.jobId())
                    .assignedEmployeeId(request.assignedEmployeeId())
                    .startTime(request.startTime())
                    .endTime(request.endTime())
                    .notes(request.notes())
                    .build();
        }

        ScheduledJob saved = scheduledJobRepository.save(jobToSave);
        return scheduleMapper.mapToScheduledJobDTO(saved);
    }

    // ... (Keep getScheduleForEmployee, getMasterScheduleByDay, etc. exactly as they were) ...

    @Override
    public List<ScheduledJobDTO> getScheduleForEmployee(UUID assignedEmployeeId) {
        return scheduledJobRepository.findByAssignedEmployeeId(assignedEmployeeId).stream()
                .map(scheduleMapper::mapToScheduledJobDTO).collect(Collectors.toList());
    }

    @Override
    public List<ScheduledJobDTO> getScheduleForEmployeeByDay(UUID assignedEmployeeId, LocalDateTime day) {

        ZoneId zone = ZoneOffset.UTC;

        LocalDate localDate = day.atZone(zone).toLocalDate();

        LocalDateTime start = localDate.atStartOfDay(zone).toLocalDateTime();
        LocalDateTime end = localDate.atTime(LocalTime.MAX).atZone(zone).toLocalDateTime();

        return scheduledJobRepository
                .findByAssignedEmployeeIdAndStartTimeBetween(assignedEmployeeId, start, end)
                .stream()
                .map(scheduleMapper::mapToScheduledJobDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<ScheduledJobDTO> getMasterScheduleByDay(LocalDateTime day) {

        ZoneId zone = ZoneOffset.UTC;

        LocalDate localDate = day.atZone(zone).toLocalDate();

        LocalDateTime start = localDate.atStartOfDay(zone).toLocalDateTime();
        LocalDateTime end = localDate.atTime(LocalTime.MAX).atZone(zone).toLocalDateTime();

        return scheduledJobRepository
                .findByStartTimeBetween(start, end)
                .stream()
                .map(scheduleMapper::mapToScheduledJobDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ScheduledJobDTO getScheduleByJobId(UUID jobId) {
        ScheduledJob job = scheduledJobRepository.findByJobId(jobId);
        if (job == null) throw new EntityNotFoundException("No schedule found for job: " + jobId);
        return scheduleMapper.mapToScheduledJobDTO(job);
    }

    @Override
    public void deleteScheduledJob(UUID scheduleId) {
        if (!scheduledJobRepository.existsById(scheduleId)) throw new EntityNotFoundException("Not found");
        scheduledJobRepository.deleteById(scheduleId);
    }
}