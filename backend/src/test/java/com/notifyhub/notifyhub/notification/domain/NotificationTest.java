package com.notifyhub.notifyhub.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void createStartsPendingWithTimestampsAndFields() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");

        assertThat(n.getId()).isNotNull();
        assertThat(n.getToEmail()).isEqualTo("a@b.com");
        assertThat(n.getSubject()).isEqualTo("Hi");
        assertThat(n.getBody()).isEqualTo("Body");
        assertThat(n.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(n.getCreatedAt()).isNotNull();
        assertThat(n.getUpdatedAt()).isNull();
    }

    @Test
    void markSentSetsStatusAndUpdatedAt() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");

        n.markSent();

        assertThat(n.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(n.getUpdatedAt()).isNotNull();
    }

    @Test
    void markFailedSetsStatusAndUpdatedAt() {
        Notification n = Notification.create("a@b.com", "Hi", "Body");

        n.markFailed();

        assertThat(n.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(n.getUpdatedAt()).isNotNull();
    }

    @Test
    void statusEnumHasAllValues() {
        assertThat(NotificationStatus.valueOf("PENDING")).isEqualTo(NotificationStatus.PENDING);
        assertThat(NotificationStatus.values())
                .containsExactly(NotificationStatus.PENDING, NotificationStatus.SENT, NotificationStatus.FAILED);
    }
}
