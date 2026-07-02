package com.notifyhub.notifyhub.notification.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.notifyhub.notifyhub.notification.domain.Branding;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeliveryMessageTest {

    @Test
    void exposesComponentsAndEventType() {
        UUID id = UUID.randomUUID();
        Branding b = Branding.of("logo", null, "Hi", null, null);
        DeliveryMessage m = new DeliveryMessage(id, "a@b.com", "Hi", "Body", b);

        assertThat(m.notificationId()).isEqualTo(id);
        assertThat(m.toEmail()).isEqualTo("a@b.com");
        assertThat(m.subject()).isEqualTo("Hi");
        assertThat(m.body()).isEqualTo("Body");
        assertThat(m.branding()).isEqualTo(b);
        assertThat(DeliveryMessage.EVENT_TYPE).isEqualTo("notification.created");
    }

    @Test
    void fourArgConstructorDefaultsToNoBranding() {
        DeliveryMessage m = new DeliveryMessage(UUID.randomUUID(), "a@b.com", "Hi", "Body");
        assertThat(m.branding()).isEqualTo(Branding.NONE);
    }
}
