package com.notifyhub.notifyhub.notification.realtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

class SseServiceTest {

    /** SseService that hands out supplied (mock) emitters instead of real ones. */
    static class TestableSseService extends SseService {
        private final Deque<SseEmitter> queue;
        TestableSseService(SseEmitter... emitters) {
            this.queue = new ArrayDeque<>(List.of(emitters));
        }
        @Override protected SseEmitter newEmitter() { return queue.poll(); }
    }

    @Test
    void subscribeSendsInitialEventAndPushDeliversToEmitter() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        TestableSseService svc = new TestableSseService(emitter);

        SseEmitter returned = svc.subscribe("user@x.com");
        svc.push("user@x.com", "notification", "payload");

        assertThat(returned).isSameAs(emitter);
        verify(emitter, times(2)).send(any(SseEventBuilder.class)); // subscribed + push
    }

    @Test
    void realServiceCreatesEmitterAndBuffersPush() {
        SseService svc = new SseService(); // exercises the real newEmitter()
        SseEmitter emitter = svc.subscribe("real@x.com");

        assertThat(emitter).isNotNull();
        // A fresh (uninitialized) emitter buffers sends without error.
        svc.push("real@x.com", "notification", "payload");
    }

    @Test
    void pushWithNoSubscribersIsANoOp() {
        TestableSseService svc = new TestableSseService(mock(SseEmitter.class));
        svc.push("ghost@x.com", "notification", "payload"); // early return, no exception
    }

    @Test
    void deadEmitterIsRemovedWhenPushFails() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        doNothing().doThrow(new IOException("broken")).when(emitter).send(any(SseEventBuilder.class));
        TestableSseService svc = new TestableSseService(emitter);

        svc.subscribe("user@x.com");      // send #1 ok
        svc.push("user@x.com", "n", "p"); // send #2 throws -> removed, key cleaned
        svc.push("user@x.com", "n", "p"); // list gone -> no further send

        verify(emitter, times(2)).send(any(SseEventBuilder.class));
    }

    @Test
    void oneDeadEmitterRemovedButOthersRemain() throws IOException {
        SseEmitter dead = mock(SseEmitter.class);
        doNothing().doThrow(new IOException("broken")).when(dead).send(any(SseEventBuilder.class));
        SseEmitter live = mock(SseEmitter.class);
        TestableSseService svc = new TestableSseService(dead, live);

        svc.subscribe("user@x.com"); // dead (send #1 ok)
        svc.subscribe("user@x.com"); // live (send #1 ok)
        svc.push("user@x.com", "n", "p");  // dead throws -> removed; live ok; list not empty
        svc.push("user@x.com", "n", "p");  // list still has live

        verify(dead, times(2)).send(any(SseEventBuilder.class));  // subscribed + failed push
        verify(live, times(3)).send(any(SseEventBuilder.class));  // subscribed + 2 pushes
    }

    @Test
    void emitterRemovedWhenInitialSendFails() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        doThrow(new IOException("broken")).when(emitter).send(any(SseEventBuilder.class));
        TestableSseService svc = new TestableSseService(emitter);

        svc.subscribe("user@x.com");      // send #1 throws -> removed in subscribe
        svc.push("user@x.com", "n", "p"); // list gone -> no send

        verify(emitter, times(1)).send(any(SseEventBuilder.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void lifecycleCallbacksRemoveEmitterAndTolerateMissingList() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        doNothing().doThrow(new IOException("broken")).when(emitter).send(any(SseEventBuilder.class));
        TestableSseService svc = new TestableSseService(emitter);

        svc.subscribe("user@x.com");

        ArgumentCaptor<Runnable> completion = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> timeout = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Consumer<Throwable>> error = ArgumentCaptor.forClass(Consumer.class);
        verify(emitter).onCompletion(completion.capture());
        verify(emitter).onTimeout(timeout.capture());
        verify(emitter).onError(error.capture());

        svc.push("user@x.com", "n", "p"); // send throws -> emitter removed, key cleaned

        // Callbacks now run against an already-removed emitter (list == null path)
        timeout.getValue().run();
        error.getValue().accept(new RuntimeException("x"));
        completion.getValue().run();

        verify(emitter, times(2)).send(any(SseEventBuilder.class));
    }
}
