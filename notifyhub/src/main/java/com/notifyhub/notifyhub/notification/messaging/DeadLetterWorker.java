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
 * Terminal handler for delivery tasks that exhausted their retries. Marks the
 * notification FAILED so its final state is observable in the database.
 */
@Component
public class DeadLetterWorker {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterWorker.class);

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    public DeadLetterWorker(NotificationRepository notificationRepository, SseService sseService) {
        this.notificationRepository = notificationRepository;
        this.sseService = sseService;
    }

    @RabbitListener(queues = RabbitConfig.DLQ)
    @Transactional
    public void handleDead(DeliveryMessage message) {
        log.error("Delivery permanently failed for notification {} -> {}",
                message.notificationId(), message.toEmail());

        notificationRepository.findById(message.notificationId()).ifPresent(notification -> {
            notification.markFailed();
            notificationRepository.save(notification);
            sseService.push(notification.getToEmail(), "notification",
                    new NotificationEvent(notification.getId(), notification.getSubject(), notification.getStatus()));
        });
    }
}
