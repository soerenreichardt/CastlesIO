package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.game.Player;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEmitters {

    public static final long EMITTER_TIMEOUT = 3600000L; //1h in milliseconds

    private final Map<UUID, SseEmitter> playerEmitters;
    private final ServerEventService serverEventService;
    private final UUID id;

    public PlayerEmitters(UUID id, ServerEventService serverEventService) {
        this.id = id;
        this.playerEmitters = new HashMap<>();
        this.serverEventService = serverEventService;
    }

    public void create(UUID playerId) {
        playerEmitters.put(playerId, createEmitter(playerId));
    }

    public SseEmitter get(UUID playerId) {
        return playerEmitters.get(playerId);
    }

    public SseEmitter getOrCreate(UUID playerId) {
        playerEmitters.remove(playerId);
        SseEmitter playerEmitter = createEmitter(playerId);
        playerEmitters.put(playerId, playerEmitter);
        return playerEmitter;
    }

    public void remove(UUID playerId) {
        playerEmitters.remove(playerId);
    }

    public void sendToPlayer(Player player, Object message) {
        SseEmitter playerSseEmitter = playerEmitters.get(player.getId());
        try {
            playerSseEmitter.send(message);
        } catch (IOException | IllegalStateException e) {
            playerSseEmitter.complete();
            serverEventService.triggerEvent(id, ServerEvent.PLAYER_DISCONNECTED, player);
        }
    }

    private SseEmitter createEmitter(UUID playerId) {
        SseEmitter sseEmitter = new SseEmitter(EMITTER_TIMEOUT);
        sseEmitter.onCompletion(() -> {
            playerEmitters.remove(playerId);
            System.out.println("removed!");
        });
        return sseEmitter;
    }
}
