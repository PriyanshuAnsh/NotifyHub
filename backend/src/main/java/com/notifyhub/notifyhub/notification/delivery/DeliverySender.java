package com.notifyhub.notifyhub.notification.delivery;

import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;

/**
 * The single seam where a notification is actually dispatched to a channel.
 * Implementations must throw on failure so the worker's retry / dead-letter
 * path can react. Selected at runtime by {@code notifyhub.delivery.mode}.
 */
public interface DeliverySender {

    /** Dispatch the notification, or throw if delivery fails. */
    void send(DeliveryMessage message);
}
