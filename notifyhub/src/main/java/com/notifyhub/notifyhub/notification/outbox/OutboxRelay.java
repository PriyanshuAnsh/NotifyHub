package com.notifyhub.notifyhub.notification.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.notifyhub.notification.domain.OutboxEvent;
import com.notifyhub.notifyhub.notification.domain.OutboxStatus;
import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import com.notifyhub.notifyhub.notification.messaging.RabbitConfig;
import com.notifyhub.notifyhub.notification.repository.OutboxEventRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Polls the transactional outbox and relays pending events to RabbitMQ.
 * A published event is marked so it is never sent twice; a failed publish
 * leaves the event PENDING for the next poll (at-least-once delivery).
 */
@Component
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private static final int BATCH_SIZE = 100;

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OutboxRelay(OutboxEventRepository outboxEventRepository,
                       RabbitTemplate rabbitTemplate,
                       ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${notifyhub.outbox.poll-interval-ms:2000}")
    public void relayPendingEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING, PageRequest.of(0, BATCH_SIZE));
        if (pending.isEmpty()) {
            return;
        }
        for (OutboxEvent event : pending) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {
        try {
            DeliveryMessage message = objectMapper.readValue(event.getPayload(), DeliveryMessage.class);
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, message);
            event.markPublished();
            outboxEventRepository.save(event);
            log.debug("Relayed outbox event {} for notification {}", event.getId(), message.notificationId());
        } catch (Exception ex) {
            // Leave the event PENDING so the next poll retries it.
            log.warn("Failed to relay outbox event {}: {}", event.getId(), ex.getMessage());
        }
    }
}
