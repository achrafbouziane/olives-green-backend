package org.project.notificationservice.service;


import org.project.notificationservice.dto.NotificationRequest;

public interface NotificationService {
    void processNotification(NotificationRequest request);
}