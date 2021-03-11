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

    public UUID joinLobby(UUID id, String playerName) throws IOException {
        var gameLobby = server.gameLobbyById(id);
        var player = new Player(playerName);
        gameLobby.addPlayer(player); // TODO: exception handling

        updateLobbyState(gameLobby);

        return player.getId();
    }

    public void updateLobbyState(GameLobby gameLobby) throws IOException{
        var sseEmitter = this.emitterService.getEmitterById(gameLobby.getId());
        sseEmitter.send(LobbyStateDTO.from(gameLobby));
    }
}
