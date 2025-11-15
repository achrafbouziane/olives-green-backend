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
    private final ScheduleMapper scheduleMapper;

    @Override
    public ScheduledJobDTO createScheduledJob(CreateScheduleRequest request) {
        // In a real app, you'd also call job-service to update the job's status to SCHEDULED

        ScheduledJob scheduledJob = ScheduledJob.builder()
                .jobId(request.jobId())
                .employeeId(request.employeeId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .notes(request.notes())
                .build();

        ScheduledJob savedJob = scheduledJobRepository.save(scheduledJob);
        return scheduleMapper.mapToScheduledJobDTO(savedJob);
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
        // In a real app, you'd also call job-service to update its status back to PENDING
        scheduledJobRepository.deleteById(scheduleId);
    }
}