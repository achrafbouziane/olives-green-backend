package org.project.jobservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobservice.domain.JobStatus;

import java.time.LocalDate;
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
    private UUID assignedEmployeeId; // From user-service

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID propertyId;

    // --- Links to this service ---
    // The Quote that this job was created from
    @OneToOne
    @JoinColumn(name = "quote_id", unique = true)
    private Quote quote;

    // --- Job Details ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    private LocalDate scheduledDate;
}