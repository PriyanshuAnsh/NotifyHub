package com.notifyhub.notifyhub.notification.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
class DeadLetterWorkerTest {

    @Mock NotificationRepository repository;
    @Mock SseService sseService;

    DeadLetterWorker worker;

    @BeforeEach
    void setUp() {
        worker = new DeadLetterWorker(repository, sseService);
    }

    @Test
    void marksFailedAndPushesWhenPresent() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        when(repository.findById(n.getId())).thenReturn(Optional.of(n));

        worker.handleDead(new DeliveryMessage(n.getId(), "a@b.com", "Hi", "Body"));

        assertThat(n.getStatus()).isEqualTo(NotificationStatus.FAILED);
        verify(repository).save(n);
        verify(sseService).push(eq("a@b.com"), eq("notification"), any());
    }

    @Test
    void doesNothingWhenNotificationMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        worker.handleDead(new DeliveryMessage(id, "a@b.com", "Hi", "Body"));

        verify(repository, never()).save(any());
        verifyNoInteractions(sseService);
    }
}
