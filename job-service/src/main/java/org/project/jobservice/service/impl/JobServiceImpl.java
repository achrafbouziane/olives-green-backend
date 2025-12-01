package org.project.jobservice.service.impl;

import org.project.jobservice.client.*;
import org.project.jobservice.domain.JobFrequency;
import org.project.jobservice.domain.JobStatus;
import org.project.jobservice.domain.NotificationChannel;
import org.project.jobservice.dto.*;
import org.project.jobservice.entity.Job;
import org.project.jobservice.entity.JobVisit;
import org.project.jobservice.entity.Quote;
import org.project.jobservice.mapper.JobMapper;
import org.project.jobservice.mapper.JobVisitMapper;
import org.project.jobservice.repository.JobRepository;
import org.project.jobservice.repository.JobVisitRepository;
import org.project.jobservice.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // Or SecurityException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final JobVisitRepository jobVisitRepository;
    private final JobVisitMapper jobVisitMapper;

    private final InvoiceClient invoiceClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    @Override
    public List<JobDTO> getAllJobs(UUID userId, String userRole) {
        List<Job> jobs;
        if ("EMPLOYEE".equals(userRole)) {
            jobs = jobRepository.findByAssignedEmployeeId(userId);
        } else {
            jobs = jobRepository.findAll();
        }
        return jobs.stream().map(this::enrichJobDTO).collect(Collectors.toList());
    }

    @Override
    public JobDTO getJobById(UUID jobId, UUID userId, String userRole) {
        Job job = findJobById(jobId);
        if ("EMPLOYEE".equals(userRole)) {
            if (job.getAssignedEmployeeId() == null || !job.getAssignedEmployeeId().equals(userId)) {
                throw new AccessDeniedException("Access Denied: You are not assigned to this job.");
            }
        }
        return enrichJobDTO(job);
    }

    @Override
    @Transactional
    public void scheduleJob(CreateScheduleRequest request, String userRole) {
        if (!"ADMIN".equals(userRole)) throw new AccessDeniedException("Access Denied");

        Job originalJob = findJobById(request.jobId());
        if (request.assignedEmployeeId() == null) throw new IllegalArgumentException("Assign an employee.");

        // 1. Setup the Group ID
        UUID batchId = UUID.randomUUID();

        // 2. Schedule the Original Job
        originalJob.setStatus(JobStatus.SCHEDULED);
        originalJob.setAssignedEmployeeId(request.assignedEmployeeId());
        originalJob.setScheduledDate(request.startTime());

        // Save metadata
        originalJob.setFrequency(request.frequency());
        if (request.frequency() != JobFrequency.ONCE) {
            originalJob.setRecurringGroupId(batchId);
        }

        jobRepository.save(originalJob); // Save the first one immediately to lock it

        // 3. Optimized Loop for Recurring Jobs
        if (request.frequency() != null && request.frequency() != JobFrequency.ONCE && request.recurrenceEndDate() != null) {

            List<Job> batchJobs = new ArrayList<>(); // <--- Optimization: Collect here
            LocalDateTime nextDate = request.startTime();
            LocalDateTime endDate = request.recurrenceEndDate();
            int count = 0;
            int MAX_JOBS = 156; // Safety limit

            while (count < MAX_JOBS) {
                // Calculate next date
                switch (request.frequency()) {
                    case WEEKLY -> nextDate = nextDate.plusWeeks(1);
                    case MONTHLY -> nextDate = nextDate.plusMonths(1);
                    default -> { } // Should not happen given the check above
                }

                // Stop if we passed the user's end date
                if (nextDate.isAfter(endDate)) break;

                // Create the clone
                Job nextJob = Job.builder()
                        .customerId(originalJob.getCustomerId())
                        .propertyId(originalJob.getPropertyId())
                        .quote(originalJob.getQuote())
                        .title(originalJob.getTitle())

                        // Snapshot Fields
                        .customerName(originalJob.getCustomerName())
                        .customerEmail(originalJob.getCustomerEmail())
                        .customerPhone(originalJob.getCustomerPhone())
                        .serviceAddress(originalJob.getServiceAddress())

                        // Recurring Tags
                        .frequency(request.frequency())
                        .recurringGroupId(batchId)

                        .assignedEmployeeId(request.assignedEmployeeId())
                        .scheduledDate(nextDate)
                        .status(JobStatus.SCHEDULED)
                        .build();

                batchJobs.add(nextJob); // Add to list instead of saving
                count++;
            }

            // 4. Batch Save (One DB Call instead of 50)
            if (!batchJobs.isEmpty()) {
                jobRepository.saveAll(batchJobs);
            }
        }
    }

    @Override
    @Transactional
    public void renewJobSeries(UUID lastJobId) {
        Job lastJob = findJobById(lastJobId);

        if (lastJob.getFrequency() == null || lastJob.getFrequency() == JobFrequency.ONCE) {
            throw new IllegalArgumentException("Cannot renew a non-recurring job.");
        }

        // 1. Determine the Start Date for the NEW series
        LocalDateTime nextDate = lastJob.getScheduledDate();

        // Move forward by one interval to start the new batch
        switch (lastJob.getFrequency()) {
            case WEEKLY -> nextDate = nextDate.plusWeeks(1);
            case MONTHLY -> nextDate = nextDate.plusMonths(1);
        }

        // 2. Reuse the same Batch ID (so they stay linked) OR generate a new one
        // Keeping the same ID is better for history tracking
        UUID batchId = lastJob.getRecurringGroupId();

        // 3. Generate the next 52 jobs (Reuse your loop logic)
        List<Job> batchJobs = new ArrayList<>();
        int count = 0;
        int MAX_JOBS = 52;

        while (count < MAX_JOBS) {

            // Create the clone
            Job nextJob = Job.builder()
                    .customerId(lastJob.getCustomerId())
                    .propertyId(lastJob.getPropertyId())
                    .quote(lastJob.getQuote())
                    .title(lastJob.getTitle())

                    // Snapshots
                    .customerName(lastJob.getCustomerName())
                    .customerEmail(lastJob.getCustomerEmail())
                    .customerPhone(lastJob.getCustomerPhone())
                    .serviceAddress(lastJob.getServiceAddress())

                    // Tags
                    .frequency(lastJob.getFrequency())
                    .recurringGroupId(batchId) // Linked to the same history
                    .assignedEmployeeId(lastJob.getAssignedEmployeeId())

                    .scheduledDate(nextDate)
                    .status(JobStatus.SCHEDULED)
                    .build();

            batchJobs.add(nextJob);
            count++;

            // Advance date for next iteration
            switch (lastJob.getFrequency()) {
                case WEEKLY -> nextDate = nextDate.plusWeeks(1);
                case MONTHLY -> nextDate = nextDate.plusMonths(1);
            }
        }

        jobRepository.saveAll(batchJobs);
    }


    @Override
    @Transactional
    public JobDTO autoScheduleJob(UUID jobId) {
        Job job = findJobById(jobId);

        if (job.getStatus() != JobStatus.PENDING) throw new IllegalStateException("Job already scheduled.");
        if (job.getAssignedEmployeeId() == null) throw new IllegalArgumentException("Assign an employee first.");

        // --- FIXED LOGIC: Start "Tomorrow", Find First Slot ---

        // Start looking from Tomorrow at 9:00 AM
        LocalDateTime searchStart = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime searchEnd = searchStart.plusDays(14); // Look 2 weeks ahead

        // Get all busy slots for this employee
        List<Job> busySlots = jobRepository.findActiveJobsBetween(searchStart, searchEnd);

        // Simple Gap Finder
        LocalDateTime candidate = searchStart;
        long jobDurationHours = 2; // Default duration

        while (candidate.isBefore(searchEnd)) {
            // Optional: Skip Sunday only? (Or remove this if you work Sundays)
            if (candidate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                candidate = candidate.plusDays(1).withHour(9).withMinute(0);
                continue;
            }

            LocalDateTime candidateEnd = candidate.plusHours(jobDurationHours);
            boolean conflict = false;

            // Check against existing jobs
            for (Job busy : busySlots) {
                LocalDateTime start = busy.getScheduledDate();
                LocalDateTime end = start.plusHours(2);

                // Overlap formula: (StartA < EndB) and (EndA > StartB)
                if (candidate.isBefore(end) && candidateEnd.isAfter(start)) {
                    conflict = true;
                    break;
                }
            }

            if (!conflict) {
                // ✅ Found the earliest available slot!
                job.setScheduledDate(candidate);
                job.setStatus(JobStatus.SCHEDULED);
                return jobMapper.mapToJobDTO(jobRepository.save(job));
            }

            // ❌ Slot busy? Try next slot (+2 hours)
            candidate = candidate.plusHours(2);

            // If it's past 5 PM (17:00), wait until 9 AM tomorrow
            if (candidate.getHour() >= 17) {
                candidate = candidate.plusDays(1).withHour(9).withMinute(0);
            }
        }

        throw new IllegalStateException("No available slots found in the next 14 days.");
    }


    @Override
    @Transactional
    public JobDTO updateJobStatus(UUID jobId, JobStatus status, String userRole) {
        if (!"ADMIN".equals(userRole)) throw new AccessDeniedException("Only Admins can change status.");

        Job job = findJobById(jobId);

        if (status == JobStatus.INVOICED && job.getStatus() != JobStatus.INVOICED) {
            createFinalInvoice(job);
        }

        job.setStatus(status);
        return enrichJobDTO(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobDTO assignEmployeeToJob(AssignEmployeeRequest request) {
        Job job = findJobById(request.jobId());
        job.setAssignedEmployeeId(request.assignedEmployeeId());
        if (job.getStatus() == JobStatus.PENDING) {
            job.setStatus(JobStatus.SCHEDULED);
        }
        Job savedJob = jobRepository.save(job);

        try {
            UserDTO employee = userClient.getUserById(request.assignedEmployeeId());
            Map<String, Object> params = new HashMap<>();
            params.put("employeeName", employee.firstName());
            params.put("jobTitle", savedJob.getTitle());
            params.put("date", savedJob.getScheduledDate() != null ? savedJob.getScheduledDate().toString() : "TBD");

            NotificationRequest email = NotificationRequest.builder()
                    .channel(NotificationChannel.EMAIL)
                    .recipient(employee.email())
                    .templateKey("job-assigned")
                    .parameters(params)
                    .build();

            notificationClient.sendNotification(email);

        } catch (Exception e) {
            System.err.println("Failed to send assignment notification: " + e.getMessage());
        }
        return enrichJobDTO(savedJob);
    }

    @Override
    @Transactional
    public JobVisitDTO checkIn(UUID jobId, UUID employeeId) {
        Job job = findJobById(jobId);

        if (!employeeId.equals(job.getAssignedEmployeeId())) throw new AccessDeniedException("Not your job.");
        if (job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.INVOICED) throw new IllegalStateException("Job finished.");

        job.setStatus(JobStatus.IN_PROGRESS);
        jobRepository.save(job);

        JobVisit visit = JobVisit.builder()
                .job(job)
                .assignedEmployeeId(employeeId)
                .checkInTime(LocalDateTime.now()) // Use Instant
                .build();

        return jobVisitMapper.toDTO(jobVisitRepository.save(visit));
    }

    @Override
    @Transactional
    public JobVisitDTO updateVisit(UUID visitId, JobVisitRequest request, UUID userId, String userRole) {
        JobVisit visit = jobVisitRepository.findById(visitId).orElseThrow(() -> new EntityNotFoundException("Log not found"));

        if (!"ADMIN".equals(userRole) && !visit.getAssignedEmployeeId().equals(userId)) {
            throw new AccessDeniedException("Cannot edit this log.");
        }

        if (request.getNotes() != null) visit.setNotes(request.getNotes());
        if (request.getTasks() != null) visit.setTasksCompleted(request.getTasks());
        if (request.getBeforePhotos() != null) visit.setBeforePhotoUrls(request.getBeforePhotos());
        if (request.getAfterPhotos() != null) visit.setAfterPhotoUrls(request.getAfterPhotos());

        if (request.getEndTime() != null) {
            visit.setCheckOutTime(request.getEndTime());
        }

        return jobVisitMapper.toDTO(jobVisitRepository.save(visit));
    }

    // Helpers...
    private Job findJobById(UUID id) { return jobRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Job not found")); }

    private JobDTO enrichJobDTO(Job job) {
        JobDTO dto = jobMapper.mapToJobDTO(job);
        List<JobVisitDTO> visitDTOs = null;
        if (job.getVisits() != null) {
            visitDTOs = job.getVisits().stream().map(jobVisitMapper::toDTO).collect(Collectors.toList());
        }
        // Use Snapshot data if available
        String clientName = job.getCustomerName() != null ? job.getCustomerName() : "Unknown Client";
        String address = job.getServiceAddress() != null ? job.getServiceAddress() : "Address Not Available";

        return dto.toBuilder().customerName(clientName).serviceAddress(address).visits(visitDTOs).build();
    }

    private void createFinalInvoice(Job job) {
        try {
            Quote quote = job.getQuote();
            if (quote == null) return;

            BigDecimal total = quote.getTotalAmount();
            BigDecimal paid = quote.getDepositAmount();
            BigDecimal balanceDue = total.subtract(paid);

            if (balanceDue.compareTo(BigDecimal.ZERO) <= 0) return;

            var lineItems = List.of(new InvoiceLineItem(
                    "Final Balance for Job #" + job.getId().toString().substring(0, 8),
                    1.0,
                    balanceDue
            ));

            var invoiceRequest = new CreateInvoiceRequest(
                    job.getId(),
                    job.getCustomerId(),
                    job.getCustomerName(),
                    job.getCustomerEmail(),
                    job.getCustomerPhone(),
                    job.getServiceAddress(),
                    balanceDue,
                    BigDecimal.ZERO,
                    "FINAL",
                    lineItems
            );
            invoiceClient.createInvoice(invoiceRequest);
        } catch (Exception e) {
            System.err.println("Failed to create final invoice: " + e.getMessage());
        }
    }
    @Override
    public List<JobDTO> getJobsForEmployee(UUID id) { return jobRepository.findByAssignedEmployeeId(id).stream().map(this::enrichJobDTO).collect(Collectors.toList()); }
    @Override
    public List<JobDTO> getJobsForCustomer(UUID id) { return jobRepository.findByCustomerId(id).stream().map(this::enrichJobDTO).collect(Collectors.toList()); }
}