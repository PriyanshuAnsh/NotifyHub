package com.notifyhub.notifyhub.notification.messaging;

import com.notifyhub.notifyhub.notification.domain.Branding;
import java.util.UUID;

/**
 * Delivery task payload carried through the outbox and RabbitMQ to the worker.
 * Optional {@link Branding} opts the email into the rich HTML template.
 */
public record DeliveryMessage(
        UUID notificationId,
        String toEmail,
        String subject,
        String body,
        Branding branding) {

    public static final String EVENT_TYPE = "notification.created";

    /** Convenience for plain (unbranded) messages. */
    public DeliveryMessage(UUID notificationId, String toEmail, String subject, String body) {
        this(notificationId, toEmail, subject, body, Branding.NONE);
    }
}
