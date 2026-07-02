package com.notifyhub.notifyhub.notification.delivery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.notifyhub.notifyhub.notification.domain.Branding;
import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class SmtpDeliverySenderTest {

    @Mock JavaMailSender mailSender;

    @Test
    void plainMessageSendsSimpleMailWithFromAndFields() {
        SmtpDeliverySender s = new SmtpDeliverySender(mailSender, "no-reply@notifyhub.local");
        DeliveryMessage msg = new DeliveryMessage(UUID.randomUUID(), "user@x.com", "Hi", "Body");

        s.send(msg);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getFrom()).isEqualTo("no-reply@notifyhub.local");
        assertThat(sent.getTo()).containsExactly("user@x.com");
        assertThat(sent.getSubject()).isEqualTo("Hi");
        assertThat(sent.getText()).isEqualTo("Body");
    }

    @Test
    void nullBrandingSendsPlainText() {
        SmtpDeliverySender s = new SmtpDeliverySender(mailSender, "no-reply@notifyhub.local");
        DeliveryMessage msg = new DeliveryMessage(UUID.randomUUID(), "user@x.com", "Hi", "Body", null);

        s.send(msg);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void brandedMessageSendsHtmlMimeMessage() {
        SmtpDeliverySender s = new SmtpDeliverySender(mailSender, "no-reply@notifyhub.local");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        DeliveryMessage msg = new DeliveryMessage(UUID.randomUUID(), "user@x.com", "Hi", "Body",
                Branding.of("https://x/logo.png", null, "Welcome", null, null));

        s.send(msg);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void htmlSendFailurePropagatesAsUnchecked() {
        SmtpDeliverySender s = new SmtpDeliverySender(mailSender, "no-reply@notifyhub.local");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doThrow(new MailSendException("smtp down")).when(mailSender).send(any(MimeMessage.class));
        DeliveryMessage msg = new DeliveryMessage(UUID.randomUUID(), "user@x.com", "Hi", "Body",
                Branding.of(null, null, "Welcome", null, null));

        assertThatThrownBy(() -> s.send(msg)).isInstanceOf(IllegalStateException.class);
    }
}
