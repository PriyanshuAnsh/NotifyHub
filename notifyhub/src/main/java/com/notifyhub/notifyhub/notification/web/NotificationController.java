package com.notifyhub.notifyhub.notification.web;

import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.service.NotificationService;
import com.notifyhub.notifyhub.notification.web.dto.CreateNotificationRequest;
import com.notifyhub.notifyhub.notification.web.dto.NotificationResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody CreateNotificationRequest request) {
        Notification created = notificationService.create(
                request.toEmail(), request.subject(), request.body());
        return ResponseEntity
                .created(URI.create("/api/v1/notifications/" + created.getId()))
                .body(NotificationResponse.from(created));
    }

    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable UUID id) {
        return NotificationResponse.from(notificationService.getById(id));
    }

    @GetMapping
    public List<NotificationResponse> list() {
        return notificationService.list().stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
