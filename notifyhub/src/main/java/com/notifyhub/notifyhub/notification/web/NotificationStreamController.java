package com.notifyhub.notifyhub.notification.web;

import com.notifyhub.notifyhub.notification.realtime.SseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationStreamController {

    private final SseService sseService;

    public NotificationStreamController(SseService sseService) {
        this.sseService = sseService;
    }

    /**
     * Opens an SSE stream for a recipient. Delivery outcomes for that user are pushed
     * as {@code notification} events while the connection is held open.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam("user") String user) {
        return sseService.subscribe(user);
    }
}
