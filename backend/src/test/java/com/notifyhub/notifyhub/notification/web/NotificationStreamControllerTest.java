package com.notifyhub.notifyhub.notification.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.notifyhub.notifyhub.notification.realtime.SseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationStreamControllerTest {

    @Mock SseService sseService;

    @Test
    void streamDelegatesToSseService() {
        SseEmitter emitter = new SseEmitter();
        when(sseService.subscribe("user@x.com")).thenReturn(emitter);

        NotificationStreamController controller = new NotificationStreamController(sseService);

        assertThat(controller.stream("user@x.com")).isSameAs(emitter);
    }
}
