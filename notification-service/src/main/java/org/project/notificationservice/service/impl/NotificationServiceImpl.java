package org.project.notificationservice.service.impl;

import org.project.notificationservice.dto.NotificationRequest;
import org.project.notificationservice.entity.NotificationTemplate;
import org.project.notificationservice.repository.NotificationTemplateRepository;
import org.project.notificationservice.service.NotificationService;
import org.project.notificationservice.service.dispatch.EmailSender;
import org.project.notificationservice.service.dispatch.SmsSender; // Import the new class
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationTemplateRepository templateRepository;
    private final EmailSender emailSender;
    private final SmsSender smsSender; // Inject the new class

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void processNotification(NotificationRequest request) {
        NotificationTemplate template = templateRepository.findByTemplateKey(request.getTemplateKey())
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + request.getTemplateKey()));

        switch (request.getChannel()) {
            case EMAIL:
                String subject = compileTemplate(template.getEmailSubject(), request.getParameters());
                String body = compileTemplate(template.getEmailBody(), request.getParameters());
                emailSender.send(fromEmail, request.getRecipient(), subject, body);
                break;

            case SMS:
                // Compile and Send SMS
                if (template.getSmsBody() != null) {
                    String smsBody = compileTemplate(template.getSmsBody(), request.getParameters());
                    smsSender.send(request.getRecipient(), smsBody);
                } else {
                    System.err.println("No SMS body found for template: " + request.getTemplateKey());
                }
                break;

            // ... other cases ...

            default:
                throw new IllegalArgumentException("Unsupported channel: " + request.getChannel());
        }
    }

    private String compileTemplate(String template, Map<String, Object> params) {
        if (template == null) return "";
        String compiled = template;
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = "{{" + entry.getKey() + "}}";
                // Handle null values gracefully
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                compiled = compiled.replace(key, value);
            }
        }
        return compiled;
    }
}