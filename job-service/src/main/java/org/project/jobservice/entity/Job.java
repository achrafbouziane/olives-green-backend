package org.project.jobservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobservice.domain.JobFrequency;
import org.project.jobservice.domain.JobStatus;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue
    private UUID id;

    // --- Links to other microservices ---
    @Column(name = "assigned_employee_id")
    private UUID assignedEmployeeId; // From user-service

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID propertyId;

    // --- Links to this service ---
    // The Quote that this job was created from
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id") // No "unique = true"
    private Quote quote;

    @Column(nullable = false)
    private String title;

    // --- Job Details ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String serviceAddress; // The property address snapshot
    private LocalDateTime scheduledDate;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobVisit> visits;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private JobFrequency frequency; // Stores 'WEEKLY', 'MONTHLY', or 'ONCE'

    @Column(name = "recurring_group_id")
    private UUID recurringGroupId;  // Links the 52 jobs together
}