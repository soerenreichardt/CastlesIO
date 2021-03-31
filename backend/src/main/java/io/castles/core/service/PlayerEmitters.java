package io.castles.core.service;

import io.castles.game.Player;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEmitters {

    public static final long EMITTER_TIMEOUT = 3600000L; //1h in milliseconds

    Map<UUID, SseEmitter> playerEmitters;

    public PlayerEmitters() {
        this.playerEmitters = new HashMap<>();
    }

    public void create(UUID playerId) {
        playerEmitters.put(playerId, createEmitter(playerId));
    }

    public SseEmitter get(UUID playerId) {
        return playerEmitters.get(playerId);
    }

    public SseEmitter getOrCreate(UUID playerId) {
        return playerEmitters.computeIfAbsent(playerId, this::createEmitter);
    }

    public void remove(UUID playerId) {
        playerEmitters.remove(playerId);
    }

    public void sendToPlayer(Player player, Object message) {
        SseEmitter playerSseEmitter = playerEmitters.get(player.getId());
        try {
            playerSseEmitter.send(message);
        } catch (IOException e) {
            playerSseEmitter.complete();
        }
    }

    private SseEmitter createEmitter(UUID playerId) {
        SseEmitter sseEmitter = new SseEmitter(EMITTER_TIMEOUT);
        sseEmitter.onCompletion(() -> playerEmitters.remove(playerId));
        return sseEmitter;
    }
}