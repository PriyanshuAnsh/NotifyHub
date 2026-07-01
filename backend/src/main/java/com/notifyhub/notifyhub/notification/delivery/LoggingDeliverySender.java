package com.notifyhub.notifyhub.notification.delivery;

import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Default sender: logs the dispatch instead of contacting a real channel.
 * A subject containing "fail" throws, so the retry / dead-letter path stays
 * demoable without a live provider. Active unless delivery mode is "smtp".
 */
@Component
@ConditionalOnProperty(name = "notifyhub.delivery.mode", havingValue = "log", matchIfMissing = true)
public class LoggingDeliverySender implements DeliverySender {

    private static final Logger log = LoggerFactory.getLogger(LoggingDeliverySender.class);

    @Override
    public void send(DeliveryMessage message) {
        log.info("[log] delivering {} -> {} | subject='{}'",
                message.notificationId(), message.toEmail(), message.subject());
        if (message.subject() != null && message.subject().toLowerCase().contains("fail")) {
            throw new IllegalStateException(
                    "Simulated delivery failure for " + message.notificationId());
        }
    }
}
