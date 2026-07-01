package com.notifyhub.notifyhub.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.domain.OutboxEvent;
import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import com.notifyhub.notifyhub.notification.repository.NotificationRepository;
import com.notifyhub.notifyhub.notification.repository.OutboxEventRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationRepository notificationRepository,
                               OutboxEventRepository outboxEventRepository,
                               ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Persists the notification and its outbox event atomically. The event is picked up
     * later by {@code OutboxRelay} and published to RabbitMQ.
     */
    @Transactional
    public Notification create(String toEmail, String subject, String body) {
        Notification notification = Notification.create(toEmail, subject, body);
        notificationRepository.save(notification);

        DeliveryMessage message = new DeliveryMessage(
                notification.getId(),
                notification.getToEmail(),
                notification.getSubject(),
                notification.getBody());
        outboxEventRepository.save(
                OutboxEvent.pending(DeliveryMessage.EVENT_TYPE, toJson(message)));

        return notification;
    }

    @Transactional(readOnly = true)
    public Notification getById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Notification> list() {
        return notificationRepository.findAll();
    }

    private String toJson(DeliveryMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize delivery message", e);
        }
    }
}
