package io.castles.core.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    public static final long EMITTER_TIMEOUT = 1000L;
    private final Map<UUID, SseEmitter> sseEmitters;

    public SseEmitterService() {
        this.sseEmitters = new ConcurrentHashMap<>();
    }

    public void createEmitter(UUID id) {
        SseEmitter sseEmitter = new SseEmitter(EMITTER_TIMEOUT);
        this.sseEmitters.put(id, sseEmitter);
        sseEmitter.onCompletion(() -> sseEmitters.remove(id));
    }

    public SseEmitter getEmitterById(UUID id) {
        return this.sseEmitters.get(id);
    }
}