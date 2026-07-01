package com.notifyhub.notifyhub.notification.realtime;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Best-effort real-time delivery over Server-Sent Events. Emitters are grouped by
 * recipient ("user"); delivery outcomes are pushed to any currently connected clients.
 * SSE is best-effort only — the database remains the source of truth.
 */
@Service
public class SseService {

    private static final Logger log = LoggerFactory.getLogger(SseService.class);
    private static final long TIMEOUT_MS = 30 * 60 * 1000L;

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String user) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        List<SseEmitter> list = emitters.computeIfAbsent(user, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> remove(user, emitter));
        emitter.onTimeout(() -> remove(user, emitter));
        emitter.onError(e -> remove(user, emitter));

        try {
            emitter.send(SseEmitter.event().name("subscribed").data(Map.of("user", user)));
        } catch (IOException e) {
            remove(user, emitter);
        }
        return emitter;
    }

    public void push(String user, String eventName, Object payload) {
        List<SseEmitter> list = emitters.get(user);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(payload));
            } catch (IOException | IllegalStateException e) {
                log.debug("Dropping dead SSE emitter for user {}: {}", user, e.getMessage());
                remove(user, emitter);
            }
        }
    }

    private void remove(String user, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(user);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(user, list);
            }
        }
    }
}
