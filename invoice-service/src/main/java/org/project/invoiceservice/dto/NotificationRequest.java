package org.project.invoiceservice.dto;


import lombok.Data;
import org.project.invoiceservice.domain.NotificationChannel;

import java.util.Map;

@Data
public class NotificationRequest {

    // 1. WHICH channel to use?
    private NotificationChannel channel; // EMAIL, SMS, WHATSAPP, PUSH

    // 2. WHO to send it to?
    private String recipient; // An email address, a phone number, or a device token

    // 3. WHAT message to send?
    private String templateKey; // e.g., "job-assigned"

    // 4. WHAT data to fill in?
    // e.g., { "employeeName": "Achraf", "jobTitle": "Spring Cleanup" }
    private Map<String, Object> parameters;
}