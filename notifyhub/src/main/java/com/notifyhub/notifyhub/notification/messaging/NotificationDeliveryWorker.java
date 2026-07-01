package com.notifyhub.notifyhub.notification.messaging;

import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.realtime.NotificationEvent;
import com.notifyhub.notifyhub.notification.realtime.SseService;
import com.notifyhub.notifyhub.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumes delivery tasks from RabbitMQ and performs the (simulated) send.
 * On success the notification is marked SENT and pushed over SSE. On failure the
 * exception propagates so the broker retries and, once attempts are exhausted,
 * dead-letters the message to the DLQ (see {@link DeadLetterWorker}).
 */
@Component
public class NotificationDeliveryWorker {

    private static final Logger log = LoggerFactory.getLogger(NotificationDeliveryWorker.class);

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    public NotificationDeliveryWorker(NotificationRepository notificationRepository,
                                      SseService sseService) {
        this.notificationRepository = notificationRepository;
        this.sseService = sseService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    @Transactional
    public void handle(DeliveryMessage message) {
        Notification notification = notificationRepository.findById(message.notificationId())
                .orElse(null);
        if (notification == null) {
            log.warn("Notification {} no longer exists; discarding delivery task", message.notificationId());
            return;
        }

        simulateSend(message);

        notification.markSent();
        notificationRepository.save(notification);
        log.info("Delivered notification {} to {}", notification.getId(), notification.getToEmail());

        sseService.push(notification.getToEmail(), "notification",
                new NotificationEvent(notification.getId(), notification.getSubject(), notification.getStatus()));
    }

    /**
     * Stand-in for a real channel provider. Throws for subjects containing "fail" so the
     * retry / dead-letter path can be exercised in a demo.
     */
    private void simulateSend(DeliveryMessage message) {
        log.info("Sending notification {} -> {} | subject='{}'",
                message.notificationId(), message.toEmail(), message.subject());
        if (message.subject() != null && message.subject().toLowerCase().contains("fail")) {
            throw new IllegalStateException("Simulated delivery failure for " + message.notificationId());
        }
    }
}
