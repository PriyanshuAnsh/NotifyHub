package com.notifyhub.notifyhub.notification.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.notifyhub.notifyhub.notification.delivery.DeliverySender;
import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.domain.NotificationStatus;
import com.notifyhub.notifyhub.notification.realtime.SseService;
import com.notifyhub.notifyhub.notification.repository.NotificationRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationDeliveryWorkerTest {

    @Mock NotificationRepository repository;
    @Mock DeliverySender deliverySender;
    @Mock SseService sseService;

    NotificationDeliveryWorker worker;

    @BeforeEach
    void setUp() {
        worker = new NotificationDeliveryWorker(repository, deliverySender, sseService);
    }

    private DeliveryMessage messageFor(Notification n) {
        return new DeliveryMessage(n.getId(), n.getToEmail(), n.getSubject(), n.getBody());
    }

    @Test
    void deliversMarksSentAndPushesSse() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(repository.findById(n.getId())).thenReturn(Optional.of(n));

        worker.handle(messageFor(n));

        verify(deliverySender).send(any(DeliveryMessage.class));
        assertThat(n.getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(repository).save(n);
        verify(sseService).push(eq("a@b.com"), eq("notification"), any());
    }

    @Test
    void discardsWhenNotificationMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        worker.handle(new DeliveryMessage(id, "a@b.com", "Hi", "Body"));

        verifyNoInteractions(deliverySender, sseService);
        verify(repository, never()).save(any());
    }

    @Test
    void propagatesWhenSenderFails() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(repository.findById(n.getId())).thenReturn(Optional.of(n));
        doThrow(new IllegalStateException("smtp down")).when(deliverySender).send(any());

        assertThatThrownBy(() -> worker.handle(messageFor(n)))
                .isInstanceOf(IllegalStateException.class);

        verify(repository, never()).save(any());
        verifyNoInteractions(sseService);
    }
}
