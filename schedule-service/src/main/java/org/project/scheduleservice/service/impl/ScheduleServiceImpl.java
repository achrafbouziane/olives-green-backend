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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduledJobRepository scheduledJobRepository;
    private final ScheduleMapper scheduleMapper; //

    @Override
    @Transactional
    public ScheduledJobDTO createScheduledJob(CreateScheduleRequest request) {
        ScheduledJob scheduledJob = ScheduledJob.builder()
                .jobId(request.jobId())
                .employeeId(request.employeeId())
                .startTime(request.startTime()) // Changed from startTime() to getStartDate() based on typical DTOs
                .endTime(request.endTime())     // Changed from endTime() to getEndDate()
                .notes(request.notes())
                .build();

        ScheduledJob saved = scheduledJobRepository.save(scheduledJob);
        return scheduleMapper.mapToScheduledJobDTO(saved);
    }

    @Override
    public List<ScheduledJobDTO> getScheduleForEmployee(UUID employeeId) {
        return scheduledJobRepository.findByEmployeeId(employeeId).stream()
                .map(scheduleMapper::mapToScheduledJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledJobDTO> getScheduleForEmployeeByDay(UUID employeeId, LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);

        return scheduledJobRepository.findByEmployeeIdAndStartTimeBetween(employeeId, startOfDay, endOfDay).stream()
                .map(scheduleMapper::mapToScheduledJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledJobDTO> getMasterScheduleByDay(LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);

        return scheduledJobRepository.findByStartTimeBetween(startOfDay, endOfDay).stream()
                .map(scheduleMapper::mapToScheduledJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduledJobDTO getScheduleByJobId(UUID jobId) {
        ScheduledJob job = scheduledJobRepository.findByJobId(jobId);
        if (job == null) {
            throw new EntityNotFoundException("No schedule found for job ID: " + jobId);
        }
        return scheduleMapper.mapToScheduledJobDTO(job);
    }

    @Override
    public void deleteScheduledJob(UUID scheduleId) {
        if (!scheduledJobRepository.existsById(scheduleId)) {
            throw new EntityNotFoundException("ScheduledJob not found with ID: " + scheduleId);
        }
        scheduledJobRepository.deleteById(scheduleId);
    }
}