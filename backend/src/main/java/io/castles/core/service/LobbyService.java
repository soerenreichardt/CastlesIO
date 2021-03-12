package io.castles.core.service;

import io.castles.core.model.LobbyStateDTO;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class LobbyService {

    private final Server server;
    private final SseEmitterService emitterService;

    public LobbyService(Server server, SseEmitterService emitterService) {
        this.server = server;
        this.emitterService = emitterService;
    }

    public UUID joinLobby(UUID id, String playerName) {
        var gameLobby = server.gameLobbyById(id);
        var player = new Player(playerName);
        gameLobby.addPlayer(player); // TODO: exception handling

        updateLobbyState(gameLobby);

        return player.getId();
    }

    public void updateLobbyState(GameLobby gameLobby) {
        var lobbySseEmitters = this.emitterService.getAllLobbyEmitters(gameLobby.getId());
        lobbySseEmitters.forEach((sseEmitter -> {
            try {
                sseEmitter.send(LobbyStateDTO.from(gameLobby));
            } catch (IOException e) {
                throw new RuntimeException("sseEmitter send throw an IOException", e);
            }
        }));
    }
}
