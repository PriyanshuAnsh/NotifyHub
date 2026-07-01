package com.notifyhub.notifyhub.notification.messaging;

import java.util.UUID;

/**
 * Delivery task payload carried through the outbox and RabbitMQ to the worker.
 */
public record DeliveryMessage(
        UUID notificationId,
        String toEmail,
        String subject,
        String body) {

    public static final String EVENT_TYPE = "notification.created";
}
