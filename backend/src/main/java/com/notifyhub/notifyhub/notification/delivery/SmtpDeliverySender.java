package com.notifyhub.notifyhub.notification.delivery;

import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Sends a real email over SMTP via Spring's {@link JavaMailSender}
 * (configured from {@code spring.mail.*}). Any send failure propagates so the
 * worker retries and eventually dead-letters. Active when
 * {@code notifyhub.delivery.mode=smtp}.
 */
@Component
@ConditionalOnProperty(name = "notifyhub.delivery.mode", havingValue = "smtp")
public class SmtpDeliverySender implements DeliverySender {

    private static final Logger log = LoggerFactory.getLogger(SmtpDeliverySender.class);

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpDeliverySender(
            JavaMailSender mailSender,
            @Value("${notifyhub.delivery.from:no-reply@notifyhub.local}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(DeliveryMessage message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(message.toEmail());
        email.setSubject(message.subject());
        email.setText(message.body());

        mailSender.send(email);
        log.info("[smtp] sent {} -> {}", message.notificationId(), message.toEmail());
    }
}
