package io.castles.core.service;

import io.castles.core.model.LobbySettingsDTO;
import io.castles.core.model.LobbyStateDTO;
import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.events.StatefulObject;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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

    public EmittingEventConsumer eventConsumerFor(GameLobby gameLobby) {
        return new EmittingEventConsumer(gameLobby);
    }

    static class EmittingEventConsumer implements StatefulObject.EventConsumer {

        private final GameLobby gameLobby;
        private final Map<UUID, SseEmitter> playerEmitters;

        public EmittingEventConsumer(GameLobby gameLobby) {
            this.gameLobby = gameLobby;
            this.playerEmitters = new HashMap<>();
        }

        @Override
        public void onPlayerAdded(Player player) {
            createEmitterForPlayer(player);
            LobbyStateDTO lobbyStateDTO = LobbyStateDTO.from(gameLobby);
            if (gameLobby.getOwnerId().equals(player.getId())) {
                lobbyStateDTO.getLobbySettings().setEditable(true);
            }
            sendToAllPlayers(lobbyStateDTO);
        }

        @Override
        public void onPlayerRemoved(Player player) {
            removePlayerEmitter(player);
            sendToAllPlayers(LobbyStateDTO.from(gameLobby));
        }

        @Override
        public void onSettingsChanged(GameLobbySettings gameLobbySettings) {
            sendToAllPlayers(LobbySettingsDTO.from(gameLobbySettings));
        }

        private void sendToAllPlayers(Object message) {
            gameLobby.getPlayers().forEach(player -> sendToPlayer(player, message));
        }

        private void sendToPlayer(Player player, Object message) {
            SseEmitter playerSseEmitter = playerEmitters.get(player.getId());
            try {
                playerSseEmitter.send(message);
            } catch (IOException e) {
                playerSseEmitter.complete();
            }
        }

        private void createEmitterForPlayer(Player player) {
            var playerId = player.getId();
            SseEmitter sseEmitter = new SseEmitter(EMITTER_TIMEOUT);
            sseEmitter.onCompletion(() -> playerEmitters.remove(playerId));
            playerEmitters.put(playerId, sseEmitter);
        }

        private void removePlayerEmitter(Player player) {
            playerEmitters.remove(player.getId());
        }
    }
}
