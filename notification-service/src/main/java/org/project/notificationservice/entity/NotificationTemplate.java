package org.project.notificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;


@Data
@Entity
@Table(name = "notification_templates")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A unique key, e.g., "job-assigned" or "invoice-sent"
    @Column(nullable = false, unique = true)
    private String templateKey;

    @Column(columnDefinition = "TEXT")
    private String emailSubject;

    @Column(columnDefinition = "TEXT")
    private String emailBody; // e.g., "Hello {{name}}, your job is... (HTML)"

    @Column(columnDefinition = "TEXT")
    private String smsBody; // e.g., "Hi {{name}}, your job is... (Plain Text)"

    @Column(columnDefinition = "TEXT")
    private String pushBody; // e.g., "New job assigned!"

    // etc. for WhatsApp
}