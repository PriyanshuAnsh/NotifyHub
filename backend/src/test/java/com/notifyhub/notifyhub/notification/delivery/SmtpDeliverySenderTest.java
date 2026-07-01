package com.notifyhub.notifyhub.notification.delivery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class SmtpDeliverySenderTest {

    @Mock JavaMailSender mailSender;

    @Test
    void sendsEmailWithConfiguredFromAndMessageFields() {
        SmtpDeliverySender sender = new SmtpDeliverySender(mailSender, "no-reply@notifyhub.local");
        DeliveryMessage msg = new DeliveryMessage(UUID.randomUUID(), "user@x.com", "Hi", "Body");

        sender.send(msg);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getFrom()).isEqualTo("no-reply@notifyhub.local");
        assertThat(sent.getTo()).containsExactly("user@x.com");
        assertThat(sent.getSubject()).isEqualTo("Hi");
        assertThat(sent.getText()).isEqualTo("Body");
    }
}
