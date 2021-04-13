package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.core.exceptions.UnableToReconnectException;
import io.castles.game.Game;
import io.castles.game.GameLobby;
import io.castles.game.Player;
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
        var player = lobby.getPlayerById(playerId);
        return reconnectPlayer(lobbyId, player);
    }

    public SseEmitter reconnectToGame(Game game, UUID playerId) throws UnableToReconnectException {
        var gameId = game.getId();
        var player = game.getPlayerById(playerId);
        return reconnectPlayer(gameId, player);
    }

    private SseEmitter reconnectPlayer(UUID lobbyOrGameId, Player player) throws UnableToReconnectException {
        var playerId = player.getId();
        serverEventService.triggerEvent(lobbyOrGameId, ServerEvent.PLAYER_RECONNECT_ATTEMPT, player);
        var playerEmitters = getPlayerEmitters(lobbyOrGameId);
        if (playerEmitters.get(playerId) == null) {
            throw new UnableToReconnectException(String.format("Player `%s` timed out", player.getName()));
        }
        SseEmitter sseEmitter = playerEmitters.recreate(playerId);
        serverEventService.triggerEvent(lobbyOrGameId, ServerEvent.PLAYER_RECONNECTED, player);
        return sseEmitter;
    }

    public void createLobbyEmitter(GameLobby lobby) {
        PlayerEmitters playerEmitters = new PlayerEmitters(lobby.getId(), serverEventService);
        sseEmitters.put(lobby.getId(), playerEmitters);
    }

    public SseEmitter getLobbyEmitterForPlayer(UUID lobbyId, UUID playerId) {
        return getPlayerEmitters(lobbyId).get(playerId);
    }

    public PlayerEmitters getPlayerEmitters(UUID lobbyOrGameId) {
        return this.sseEmitters.get(lobbyOrGameId);
    }
}
