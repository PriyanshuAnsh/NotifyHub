package com.notifyhub.notifyhub.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.domain.NotificationStatus;
import com.notifyhub.notifyhub.notification.domain.OutboxEvent;
import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import com.notifyhub.notifyhub.notification.repository.NotificationRepository;
import com.notifyhub.notifyhub.notification.repository.OutboxEventRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository notificationRepository;
    @Mock OutboxEventRepository outboxEventRepository;

    NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(notificationRepository, outboxEventRepository, new ObjectMapper());
    }

    @Test
    void createPersistsNotificationAndOutboxEventWithJsonPayload() {
        Notification created = service.create("a@b.com", "Hi", "Body");

        assertThat(created.getStatus()).isEqualTo(NotificationStatus.PENDING);
        verify(notificationRepository).save(created);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());
        OutboxEvent event = captor.getValue();
        assertThat(event.getEventType()).isEqualTo(DeliveryMessage.EVENT_TYPE);
        assertThat(event.getPayload())
                .contains(created.getId().toString())
                .contains("a@b.com")
                .contains("Hi");
    }

    @Test
    void createWrapsSerializationFailure() throws Exception {
        ObjectMapper failing = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("boom") {};
            }
        };
        NotificationService s = new NotificationService(notificationRepository, outboxEventRepository, failing);

        assertThatThrownBy(() -> s.create("a@b.com", "Hi", "Body"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("serialize");
    }

    @Test
    void getByIdReturnsWhenPresent() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(notificationRepository.findById(n.getId())).thenReturn(Optional.of(n));

        assertThat(service.getById(n.getId())).isSameAs(n);
    }

    @Test
    void getByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void listDelegatesToRepository() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(notificationRepository.findAll()).thenReturn(List.of(n));

        assertThat(service.list()).containsExactly(n);
    }
}
