package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.core.exceptions.UnableToReconnectException;
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

    public SseEmitter reconnectToLobby(GameLobby lobby, UUID playerId) throws UnableToReconnectException {
        var lobbyId = lobby.getId();
        serverEventService.triggerEvent(lobbyId, ServerEvent.PLAYER_RECONNECT_ATTEMPT, lobby.getPlayerById(playerId));
        var playerEmitters = getPlayerEmitters(lobbyId);
        if (playerEmitters.get(playerId) == null) {
            throw new UnableToReconnectException(String.format("Player `%s` timed out", lobby.getPlayerById(playerId).getName()));
        }
        SseEmitter sseEmitter = playerEmitters.getOrCreate(playerId);
        serverEventService.triggerEvent(lobbyId, ServerEvent.PLAYER_RECONNECTED, lobby.getPlayerById(playerId));
        return sseEmitter;
    }

    public void createLobbyEmitter(GameLobby lobby) {
        PlayerEmitters playerEmitters = new PlayerEmitters(lobby.getId(), serverEventService);
        sseEmitters.put(lobby.getId(), playerEmitters);
    }

    public SseEmitter getLobbyEmitterForPlayer(UUID lobbyId, UUID playerId) {
        return getPlayerEmitters(lobbyId).get(playerId);
    }

    public PlayerEmitters getPlayerEmitters(UUID lobbyId) {
        return this.sseEmitters.get(lobbyId);
    }
}
