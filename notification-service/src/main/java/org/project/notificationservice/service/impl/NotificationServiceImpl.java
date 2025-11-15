package org.project.notificationservice.service.impl;


import org.project.notificationservice.dto.NotificationRequest;
import org.project.notificationservice.entity.NotificationTemplate;
import org.project.notificationservice.repository.NotificationTemplateRepository;
import org.project.notificationservice.service.NotificationService;
import org.project.notificationservice.service.dispatch.EmailSender;
// Import your other senders
// import org.project.notificationservice.service.dispatch.SmsSender;
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
    // @Autowired private final SmsSender smsSender;
    // @Autowired private final PushSender pushSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void processNotification(NotificationRequest request) {
        // 1. Find the template
        NotificationTemplate template = templateRepository.findByTemplateKey(request.getTemplateKey())
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + request.getTemplateKey()));

        // 2. Route based on the requested channel
        switch (request.getChannel()) {
            case EMAIL:
                // 3. Compile the template
                String subject = compileTemplate(template.getEmailSubject(), request.getParameters());
                String body = compileTemplate(template.getEmailBody(), request.getParameters());

                // 4. Dispatch
                emailSender.send(fromEmail, request.getRecipient(), subject, body);
                break;

            case SMS:
                // String smsBody = compileTemplate(template.getSmsBody(), request.getParameters());
                // smsSender.send(request.getRecipient(), smsBody);
                break;

            case WHATSAPP:
                // ...
                break;

            case PUSH:
                // ...
                break;

            default:
                throw new IllegalArgumentException("Unsupported channel: " + request.getChannel());
        }
    }

    /**
     * A simple "template compiler" that replaces {{placeholders}}.
     * In a real app, you would use a library like FreeMarker or Thymeleaf.
     */
    private String compileTemplate(String template, Map<String, Object> params) {
        String compiled = template;
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = "{{" + entry.getKey() + "}}";
                String value = entry.getValue().toString();
                compiled = compiled.replace(key, value);
            }
        }
        return compiled;
    }
}