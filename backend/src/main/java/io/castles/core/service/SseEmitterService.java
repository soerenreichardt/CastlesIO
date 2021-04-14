package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.core.exceptions.UnableToReconnectException;
import io.castles.game.GameLobby;
import io.castles.game.IdentifiableObject;
import io.castles.game.PlayerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.NoSuchElementException;
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

    public <T extends IdentifiableObject & PlayerContainer> SseEmitter reconnectPlayer(T gameObject, UUID playerId) throws UnableToReconnectException {
        if (!gameObject.containsPlayer(playerId)) {
            throw new NoSuchElementException(String.format("No player with id %s found", playerId));
        }

        var id = gameObject.getId();
        var player = gameObject.getPlayerById(playerId);
        serverEventService.triggerEvent(id, ServerEvent.PLAYER_RECONNECT_ATTEMPT, player);
        var playerEmitters = getPlayerEmitters(id);
        if (!playerEmitters.contains(playerId)) {
            throw new UnableToReconnectException(String.format("Player `%s` timed out", player.getName()));
        }
        SseEmitter sseEmitter = playerEmitters.recreate(playerId);
        serverEventService.triggerEvent(id, ServerEvent.PLAYER_RECONNECTED, player);
        return sseEmitter;
    }

    public void createLobbyEmitter(GameLobby lobby) {
        PlayerEmitters playerEmitters = new PlayerEmitters(lobby.getId(), serverEventService);
        sseEmitters.put(lobby.getId(), playerEmitters);
    }

    public SseEmitter getGameObjectEmitterForPlayer(UUID gameObjectId, UUID playerId) {
        return getPlayerEmitters(gameObjectId).get(playerId);
    }

    public PlayerEmitters getPlayerEmitters(UUID lobbyOrGameId) {
        return this.sseEmitters.get(lobbyOrGameId);
    }
}
