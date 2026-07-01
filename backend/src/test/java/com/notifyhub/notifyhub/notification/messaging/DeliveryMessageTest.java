package com.notifyhub.notifyhub.notification.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeliveryMessageTest {

    @Test
    void exposesComponentsAndEventType() {
        UUID id = UUID.randomUUID();
        DeliveryMessage m = new DeliveryMessage(id, "a@b.com", "Hi", "Body");

        assertThat(m.notificationId()).isEqualTo(id);
        assertThat(m.toEmail()).isEqualTo("a@b.com");
        assertThat(m.subject()).isEqualTo("Hi");
        assertThat(m.body()).isEqualTo("Body");
        assertThat(DeliveryMessage.EVENT_TYPE).isEqualTo("notification.created");
    }
}
