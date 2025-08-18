package com.example.greenroutine.repository;

import com.example.greenroutine.api.dto.alert.AlertDto;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository

public class AlertSseRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, AlertDto.AlertEvent> eventCache = new ConcurrentHashMap<>();

    public SseEmitter saveEmitter(String key, SseEmitter emitter) {
        emitters.put(key, emitter);
        return emitter;
    }

    public void removeEmitter(String key) {
        emitters.remove(key);
    }

    public Map<String, SseEmitter> findAllEmitterStartsWith(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.unmodifiableMap(emitters);
        }
        return emitters.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void saveEventCache(String key, AlertDto.AlertEvent event) {
        eventCache.put(key, event);
    }

    public Map<String, AlertDto.AlertEvent> findAllEventCacheStartsWith(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.unmodifiableMap(eventCache);
        }
        return eventCache.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void removeEventCache(String key) {
        eventCache.remove(key);
    }

    public void clearEventCacheStartsWith(String prefix) {
        if (prefix == null || prefix.isEmpty()) return;
        eventCache.keySet().removeIf(k -> k.startsWith(prefix));
    }

}
