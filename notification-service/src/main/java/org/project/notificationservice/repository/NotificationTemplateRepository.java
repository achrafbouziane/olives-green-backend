package org.project.notificationservice.repository;

import org.project.notificationservice.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    // Find the template by its unique key
    Optional<NotificationTemplate> findByTemplateKey(String templateKey);
}
