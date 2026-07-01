package com.notifyhub.notifyhub.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OutboxEventTest {

    @Test
    void pendingInitializesFields() {
        OutboxEvent e = OutboxEvent.pending("notification.created", "{\"a\":1}");

        assertThat(e.getId()).isNotNull();
        assertThat(e.getEventType()).isEqualTo("notification.created");
        assertThat(e.getPayload()).isEqualTo("{\"a\":1}");
        assertThat(e.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(e.getCreatedAt()).isNotNull();
        assertThat(e.getPublishedAt()).isNull();
    }

    @Test
    void markPublishedSetsStatusAndPublishedAt() {
        OutboxEvent e = OutboxEvent.pending("t", "{}");

        e.markPublished();

        assertThat(e.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
        assertThat(e.getPublishedAt()).isNotNull();
    }

    @Test
    void markFailedSetsStatus() {
        OutboxEvent e = OutboxEvent.pending("t", "{}");

        e.markFailed();

        assertThat(e.getStatus()).isEqualTo(OutboxStatus.FAILED);
    }

    @Test
    void statusEnumHasAllValues() {
        assertThat(OutboxStatus.values())
                .containsExactly(OutboxStatus.PENDING, OutboxStatus.PUBLISHED, OutboxStatus.FAILED);
    }
}
