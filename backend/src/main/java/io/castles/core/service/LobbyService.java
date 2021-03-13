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

    public void joinLobby(UUID id, Player player) throws IOException {
        var gameLobby = server.gameLobbyById(id);
        gameLobby.addPlayer(player); // TODO: exception handling

        var playerId = player.getId();
        emitterService.createPlayerEmitterForLobby(id, playerId);

        updateLobbyState(gameLobby);
    }

    public void updateLobbyState(GameLobby gameLobby) throws IOException {
        var lobbySseEmitters = this.emitterService.getAllLobbyEmitters(gameLobby.getId());
        for (var playerSseEmitter : lobbySseEmitters) {
            playerSseEmitter.send(LobbyStateDTO.from(gameLobby));
        }
    }
}
