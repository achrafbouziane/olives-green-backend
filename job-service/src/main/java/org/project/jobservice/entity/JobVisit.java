package org.project.jobservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_visits")
public class JobVisit {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // ✅ Renamed from employeeId
    @Column(name = "assigned_employee_id", nullable = false)
    private UUID assignedEmployeeId;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @Column(length = 1000)
    private String notes;

    @ElementCollection
    @CollectionTable(name = "visit_tasks", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "task")
    private List<String> tasksCompleted;

    @ElementCollection
    @CollectionTable(name = "visit_photos_before", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "photo_url")
    private List<String> beforePhotoUrls;

    @ElementCollection
    @CollectionTable(name = "visit_photos_after", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "photo_url")
    private List<String> afterPhotoUrls;
}