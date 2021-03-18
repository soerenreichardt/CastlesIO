package io.castles.core.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    public static final long EMITTER_TIMEOUT = 3600000L; //1h in milliseconds
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

    public SseEmitter getLobbyEmitterForPlayer(UUID lobbyId, UUID playerId) {
        return this.sseEmitters.get(lobbyId).get(playerId);
    }

    public Collection<SseEmitter> getAllLobbyEmitters(UUID lobbyId) {
        if (this.sseEmitters.containsKey(lobbyId)) {
            return this.sseEmitters.get(lobbyId).values();
        }
        return List.of();
    }
}
