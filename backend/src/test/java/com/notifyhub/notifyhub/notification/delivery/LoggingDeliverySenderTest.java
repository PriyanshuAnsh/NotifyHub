package com.notifyhub.notifyhub.notification.delivery;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LoggingDeliverySenderTest {

    final LoggingDeliverySender sender = new LoggingDeliverySender();

    private DeliveryMessage withSubject(String subject) {
        return new DeliveryMessage(UUID.randomUUID(), "a@b.com", subject, "Body");
    }

    @Test
    void normalSubjectDeliversWithoutError() {
        assertThatCode(() -> sender.send(withSubject("Welcome"))).doesNotThrowAnyException();
    }

    @Test
    void subjectContainingFailThrows() {
        assertThatThrownBy(() -> sender.send(withSubject("please FAIL this")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void nullSubjectDeliversWithoutError() {
        assertThatCode(() -> sender.send(withSubject(null))).doesNotThrowAnyException();
    }
}
