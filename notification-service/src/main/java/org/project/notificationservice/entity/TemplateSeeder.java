package org.project.notificationservice.entity;

import lombok.RequiredArgsConstructor;
import org.project.notificationservice.repository.NotificationTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemplateSeeder implements CommandLineRunner {

    private final NotificationTemplateRepository repo;

    @Override
    public void run(String... args) {
        seed("estimate-sent-email",
                "Your Estimate from OlivesGreen is Ready",
                "<html>...</html>",
                null);

        seed("estimate-sent-sms",
                null,
                null,
                "Hi {{customerName}}, your estimate \"{{quoteTitle}}\" is ready. Review it here: {{magicLink}} - OlivesGreen");
    }

    private void seed(String key, String emailSubject, String emailBody, String smsBody) {
        if (repo.findByTemplateKey(key).isEmpty()) {
            NotificationTemplate t = NotificationTemplate.builder()
                    .templateKey(key)
                    .emailSubject(emailSubject)
                    .emailBody(emailBody)
                    .smsBody(smsBody)
                    .build();
            repo.save(t);
            System.out.println("Inserted template: " + key);
        }
    }
}
