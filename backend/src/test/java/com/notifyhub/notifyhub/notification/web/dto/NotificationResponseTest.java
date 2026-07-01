package com.notifyhub.notifyhub.notification.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.notifyhub.notifyhub.notification.domain.Notification;
import com.notifyhub.notifyhub.notification.domain.NotificationStatus;
import org.junit.jupiter.api.Test;

class NotificationResponseTest {

    @Test
    void fromCopiesEveryField() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");
        n.markSent();

        NotificationResponse r = NotificationResponse.from(n);

        assertThat(r.id()).isEqualTo(n.getId());
        assertThat(r.toEmail()).isEqualTo("a@b.com");
        assertThat(r.subject()).isEqualTo("Hi");
        assertThat(r.body()).isEqualTo("Body");
        assertThat(r.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(r.createdAt()).isEqualTo(n.getCreatedAt());
        assertThat(r.updatedAt()).isEqualTo(n.getUpdatedAt());
    }
}
