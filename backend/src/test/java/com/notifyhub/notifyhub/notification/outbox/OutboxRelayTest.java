package com.notifyhub.notifyhub.notification.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.notifyhub.notification.domain.OutboxEvent;
import com.notifyhub.notifyhub.notification.domain.OutboxStatus;
import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import com.notifyhub.notifyhub.notification.messaging.RabbitConfig;
import com.notifyhub.notifyhub.notification.repository.OutboxEventRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OutboxRelayTest {

    @Mock OutboxEventRepository repository;
    @Mock RabbitTemplate rabbitTemplate;

    final ObjectMapper mapper = new ObjectMapper();
    OutboxRelay relay;

    @BeforeEach
    void setUp() {
        relay = new OutboxRelay(repository, rabbitTemplate, mapper);
    }

    private OutboxEvent pendingEvent() throws Exception {
        DeliveryMessage msg = new DeliveryMessage(UUID.randomUUID(), "a@b.com", "Hi", "Body");
        return OutboxEvent.pending(DeliveryMessage.EVENT_TYPE, mapper.writeValueAsString(msg));
    }

    @Test
    void doesNothingWhenNoPendingEvents() {
        when(repository.findByStatusOrderByCreatedAtAsc(eq(OutboxStatus.PENDING), any(Pageable.class)))
                .thenReturn(List.of());

        relay.relayPendingEvents();

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void publishesAndMarksPublished() throws Exception {
        OutboxEvent event = pendingEvent();
        when(repository.findByStatusOrderByCreatedAtAsc(eq(OutboxStatus.PENDING), any(Pageable.class)))
                .thenReturn(List.of(event));

        relay.relayPendingEvents();

        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(RabbitConfig.ROUTING_KEY),
                any(DeliveryMessage.class));
        verify(repository).save(event);
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
    }

    @Test
    void leavesEventPendingWhenPublishFails() {
        OutboxEvent broken = OutboxEvent.pending(DeliveryMessage.EVENT_TYPE, "not-json");
        when(repository.findByStatusOrderByCreatedAtAsc(eq(OutboxStatus.PENDING), any(Pageable.class)))
                .thenReturn(List.of(broken));

        relay.relayPendingEvents();

        verify(repository, never()).save(any());
        assertThat(broken.getStatus()).isEqualTo(OutboxStatus.PENDING);
    }
}
