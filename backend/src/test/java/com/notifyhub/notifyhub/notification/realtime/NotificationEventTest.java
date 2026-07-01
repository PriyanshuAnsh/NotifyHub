package com.notifyhub.notifyhub.notification.realtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.notifyhub.notifyhub.notification.domain.NotificationStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotificationEventTest {

    @Test
    void exposesComponents() {
        UUID id = UUID.randomUUID();
        NotificationEvent e = new NotificationEvent(id, "Hi", NotificationStatus.SENT);

        assertThat(e.notificationId()).isEqualTo(id);
        assertThat(e.subject()).isEqualTo("Hi");
        assertThat(e.status()).isEqualTo(NotificationStatus.SENT);
    }
}
