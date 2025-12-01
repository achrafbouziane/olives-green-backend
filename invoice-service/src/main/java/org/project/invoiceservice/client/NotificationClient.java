package org.project.invoiceservice.client;

import org.project.invoiceservice.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// "notification-service" must match the spring.application.name of your notification service
@FeignClient(name = "notification-service", path = "/api/v1/notify")
public interface NotificationClient {

    @PostMapping
    void sendNotification(@RequestBody NotificationRequest request);
}