package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.core.events.ServerEventConsumer;
import io.castles.game.GameLobby;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    private final ServerEventService serverEventService;
    private final Map<UUID, PlayerEmitters> sseEmitters;

    public SseEmitterService(ServerEventService serverEventService) {
        this.serverEventService = serverEventService;
        this.sseEmitters = new ConcurrentHashMap<>();
    }

    public SseEmitter connectToLobby(UUID lobbyId, UUID playerId) {
        var playerEmitter = this.sseEmitters.get(lobbyId);
        var emitter = playerEmitter.getOrCreate(playerId);
        serverEventService.triggerEvent(lobbyId, ServerEvent.PLAYER_RECONNECTED, playerId);
        return emitter;
    }

    public void createPlayerEmitterForLobby(UUID lobbyId, UUID playerId) {
        sseEmitters.get(lobbyId).create(playerId);
    }

    public void createLobbyEmitter(GameLobby lobby) {
        PlayerEmitters playerEmitters = new PlayerEmitters();
        sseEmitters.put(lobby.getId(), playerEmitters);
    }

    public SseEmitter getLobbyEmitterForPlayer(UUID lobbyId, UUID playerId) {
        return this.sseEmitters.get(lobbyId).get(playerId);
    }

    public ServerEventConsumer eventConsumerForLobby(GameLobby gameLobby) {
        return new EmittingEventConsumer(gameLobby, sseEmitters.get(gameLobby.getId()));
    }
}
