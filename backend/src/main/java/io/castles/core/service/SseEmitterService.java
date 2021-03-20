package io.castles.core.service;

import io.castles.core.events.ServerEventConsumer;
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

    private final Map<UUID, EmittingEventConsumer> sseEmitters;

    public SseEmitterService() {
        this.sseEmitters = new ConcurrentHashMap<>();
    }

    public SseEmitter connectToLobby(UUID lobbyId, UUID playerId) {
        var emittingEventConsumer = this.sseEmitters.get(lobbyId);
        if (!emittingEventConsumer.playerEmitters().containsKey(playerId)) {
            emittingEventConsumer.createEmitterForPlayer(playerId);
        }
        emittingEventConsumer.onPlayerReconnected(playerId);
        return emittingEventConsumer.playerEmitters().get(playerId);
    }

    public void createPlayerEmitterForLobby(UUID lobbyId, UUID playerId) {
        sseEmitters.get(lobbyId).createEmitterForPlayer(playerId);
    }

    public SseEmitter getLobbyEmitterForPlayer(UUID lobbyId, UUID playerId) {
        return this.sseEmitters.get(lobbyId).playerEmitters().get(playerId);
    }

    public StatefulObject.EventConsumer eventConsumerFor(GameLobby gameLobby) {
        var emittingEventConsumer = new EmittingEventConsumer(gameLobby);
        sseEmitters.put(gameLobby.getId(), emittingEventConsumer);
        return emittingEventConsumer;
    }

    static class EmittingEventConsumer implements StatefulObject.EventConsumer {

        public static final long EMITTER_TIMEOUT = 3600000L; //1h in milliseconds

        private final GameLobby gameLobby;
        private final Map<UUID, SseEmitter> playerEmitters;

        public EmittingEventConsumer(GameLobby gameLobby) {
            this.gameLobby = gameLobby;
            this.playerEmitters = new HashMap<>();
        }

        public Map<UUID, SseEmitter> playerEmitters() {
            return playerEmitters;
        }

        public void onPlayerReconnected(UUID playerId) {
            sendToAllPlayers(String.format("Player %s connected", gameLobby.getPlayerById(playerId)));
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

        void createEmitterForPlayer(Player player) {
            createEmitterForPlayer(player.getId());
        }

        void createEmitterForPlayer(UUID playerId) {
            SseEmitter sseEmitter = new SseEmitter(EMITTER_TIMEOUT);
            sseEmitter.onCompletion(() -> playerEmitters.remove(playerId));
            playerEmitters.put(playerId, sseEmitter);
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

        private void removePlayerEmitter(Player player) {
            removePlayerEmitter(player.getId());
        }

        private void removePlayerEmitter(UUID playerId) {
            playerEmitters.remove(playerId);
        }
    }
}
