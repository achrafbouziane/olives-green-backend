package org.project.notificationservice.controller;


import org.project.notificationservice.dto.NotificationRequest;
import org.project.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * This is the single endpoint for all microservices to call.
     */
    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        // We run this async so the calling service gets an instant response
        // In a real app, you'd put this message on a RabbitMQ queue
        new Thread(() -> {
            notificationService.processNotification(request);
        }).start();

        return new ResponseEntity<>(HttpStatus.ACCEPTED); // 202 - Accepted
    }
}