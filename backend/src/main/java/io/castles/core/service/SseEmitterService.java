package io.castles.core.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    public static final long EMITTER_TIMEOUT = 1000L;
    private final Map<UUID, Map<UUID, SseEmitter>> sseEmitters;

    public SseEmitterService() {
        this.sseEmitters = new ConcurrentHashMap<>();
    }

    public void createPlayerEmitterForLobby(UUID lobbyId, UUID playerId) {
        SseEmitter sseEmitter = new SseEmitter(EMITTER_TIMEOUT);
        sseEmitter.onCompletion(() -> sseEmitters.get(lobbyId).remove(playerId));
        Map<UUID, SseEmitter> lobbyEmitters = sseEmitters.computeIfAbsent(lobbyId, id -> new ConcurrentHashMap<>());
        lobbyEmitters.put(playerId, sseEmitter);
    }

    public SseEmitter getEmitterByIds(UUID lobbyId, UUID playerId) {
        return this.sseEmitters.get(lobbyId).get(playerId);
    }
}
