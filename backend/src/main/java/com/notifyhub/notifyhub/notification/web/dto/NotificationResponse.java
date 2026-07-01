package com.notifyhub.notifyhub.notification.web.dto;

import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.domain.NotificationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String toEmail,
        String subject,
        String body,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getToEmail(),
                n.getSubject(),
                n.getBody(),
                n.getStatus(),
                n.getCreatedAt(),
                n.getUpdatedAt());
    }
}
