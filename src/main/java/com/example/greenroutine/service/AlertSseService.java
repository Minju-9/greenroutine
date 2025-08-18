package com.example.greenroutine.service;

import com.example.greenroutine.api.dto.alert.AlertDto;
import com.example.greenroutine.repository.AlertSseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertSseService {
    private static final long DEFAULT_TIMEOUT = 60L * 60 * 1000;
    private static final long RECONNECT_TIME  = 3000L;

    private final AlertSseRepository emitterRepository;

    public SseEmitter connect(final Long userId, final String lastEventId) {
        final String emitterId = buildEventId(userId);
        final SseEmitter emitter = emitterRepository.saveEmitter(emitterId, new SseEmitter(DEFAULT_TIMEOUT));


        emitter.onCompletion(() -> {
            log.info("[SSE] onCompletion: {}", emitterId);
            emitterRepository.removeEmitter(emitterId);
        });
        emitter.onTimeout(() -> {
            log.info("[SSE] onTimeout: {}", emitterId);
            emitter.complete();
            emitterRepository.removeEmitter(emitterId);
        });
        emitter.onError(t -> {
            log.warn("[SSE] onError: {} cause={}", emitterId, t.toString());
            emitter.completeWithError(t);
            emitterRepository.removeEmitter(emitterId);
        });

        sendToClient(emitterId, emitter, /*eventId*/emitterId, "connected",
                "알림 서버 연결 성공 [userId=" + userId + "]");

        if (lastEventId != null && !lastEventId.isEmpty()) {
            Map<String, AlertDto.AlertEvent> cached = emitterRepository.findAllEventCacheStartsWith(userId + "_");
            cached.entrySet().stream()
                    .filter(e -> isAfter(lastEventId, e.getKey()))
                    .forEach(e -> sendToClient(
                            /*emitterId for cleanup*/emitterId,
                            emitter,
                            /*eventId displayed to client*/e.getKey(),
                            "alert",
                            e.getValue()
                    ));
        }

        return emitter;
    }

    @Async
    public void send(final AlertDto.AlertEvent event) {
        // 1) 고유 이벤트 ID
        final String eventId = buildEventId(event.getUserId());

        // 2) 재전송 대비
        emitterRepository.saveEventCache(eventId, event);

        // 3) 탭/브라우저
        Map<String, SseEmitter> emitters =
                emitterRepository.findAllEmitterStartsWith(event.getUserId() + "_");

        emitters.forEach((emitterId, emitter) -> {
            try {
                sendToClient(emitterId, emitter, eventId, "alert", event);
            } catch (Exception ex) {
                log.error("[SSE] Failed to send. emitterId={}", emitterId, ex);
                safeCloseAndRemove(emitterId, emitter, ex);
            }
        });
    }

    private void sendToClient(final String emitterId,
                              final SseEmitter emitter,
                              final String eventId,
                              final String eventName,
                              final Object payload) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name(eventName)
                    .data(payload)
                    .reconnectTime(RECONNECT_TIME));
        } catch (IOException e) {
            log.warn("[SSE] send failed. emitterId={}", emitterId, e);
            safeCloseAndRemove(emitterId, emitter, e);
        }
    }

    private void safeCloseAndRemove(String emitterId, SseEmitter emitter, Throwable t) {
        try {
            emitter.completeWithError(t);
        } catch (Throwable ignored) { /* no-op */ }
        emitterRepository.removeEmitter(emitterId);
    }

    private String buildEventId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    private boolean isAfter(String lastId, String currentId) {
        long lastTs = extractTimestamp(lastId);
        long curTs  = extractTimestamp(currentId);
        return curTs > lastTs;
    }

    private long extractTimestamp(String eventId) {
        int idx = eventId.lastIndexOf('_');
        if (idx < 0 || idx + 1 >= eventId.length()) return -1L;
        try {
            return Long.parseLong(eventId.substring(idx + 1));
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}
