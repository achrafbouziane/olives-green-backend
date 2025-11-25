package org.project.jobservice.client;

import org.project.jobservice.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// "notification-service" matches the application.name in notification-service's yml
@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/v1/notify")
    void sendNotification(@RequestBody NotificationRequest request);
}