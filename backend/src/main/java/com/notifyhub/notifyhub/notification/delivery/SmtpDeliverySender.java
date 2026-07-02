package com.notifyhub.notifyhub.notification.delivery;

import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Sends a real email over SMTP via Spring's {@link JavaMailSender}. Messages with
 * branding are rendered as HTML through {@link EmailTemplate}; plain messages use
 * a simple text email. Any failure propagates so the worker retries / dead-letters.
 * Active when {@code notifyhub.delivery.mode=smtp}.
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
        boolean rich = message.branding() != null && message.branding().hasContent();
        if (rich) {
            sendHtml(message);
        } else {
            sendPlain(message);
        }
        log.info("[smtp] sent {} -> {} ({})",
                message.notificationId(), message.toEmail(), rich ? "html" : "text");
    }

    private void sendPlain(DeliveryMessage message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(message.toEmail());
        email.setSubject(message.subject());
        email.setText(message.body());
        mailSender.send(email);
    }

    private void sendHtml(DeliveryMessage message) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(message.toEmail());
            helper.setSubject(message.subject());
            helper.setText(EmailTemplate.render(message), true);
            mailSender.send(mime);
        } catch (Exception e) {
            // Surface as unchecked so the retry / dead-letter path reacts.
            throw new IllegalStateException(
                    "Failed to build HTML email for " + message.notificationId(), e);
        }
    }
}
