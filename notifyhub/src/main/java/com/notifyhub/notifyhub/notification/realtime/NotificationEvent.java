package com.notifyhub.notifyhub.notification.realtime;

import com.notifyhub.notifyhub.notification.domain.NotificationStatus;
import java.util.UUID;

/**
 * Lightweight payload pushed to SSE clients when a notification's delivery state changes.
 */
public record NotificationEvent(
        UUID notificationId,
        String subject,
        NotificationStatus status) {
}
