package com.example.greenroutine.api.alert;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseHub {

    // userId -> emitters
    private final Map<Long, Set<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    // 기본 타임아웃: 30분
    private static final long TIMEOUT_MILLIS = 30 * 60 * 1000L;

    public SseEmitter subscribe(Long userId) throws IOException {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);
        subscribers.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        // 초기 연결 확인용 이벤트(id/heartbeat)
        sendHeartbeat(emitter);

        return emitter;
    }

    public void broadcast(Long userId, String eventName, Object payload) {
        Set<SseEmitter> set = subscribers.getOrDefault(userId, Collections.emptySet());
        List<SseEmitter> dead = new ArrayList<>();

        for (SseEmitter emitter : set) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .name(eventName)
                        .id(String.valueOf(Instant.now().toEpochMilli()))
                        .data(payload, MediaType.APPLICATION_JSON);
                emitter.send(event);
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        dead.forEach(em -> remove(userId, em));
    }

    public void heartbeatAll() {
        subscribers.forEach((uid, set) -> {
            List<SseEmitter> dead = new ArrayList<>();
            for (SseEmitter emitter : set) {
                try { sendHeartbeat(emitter); }
                catch (Exception e) { dead.add(emitter); }
            }
            dead.forEach(em -> remove(uid, em));
        });
    }

    private void sendHeartbeat(SseEmitter emitter) throws IOException {
        emitter.send(SseEmitter.event()
                .name("heartbeat")
                .data("{\"ts\":" + System.currentTimeMillis() + "}"));
    }

    private void remove(Long userId, SseEmitter emitter) {
        Set<SseEmitter> set = subscribers.get(userId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) subscribers.remove(userId);
        }
    }
}
