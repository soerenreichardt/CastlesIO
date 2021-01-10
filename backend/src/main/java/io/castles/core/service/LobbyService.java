package io.castles.core.service;

import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    public SseEmitter joinLobby(UUID id, String playerName) throws IOException {
        var gameLobby = server.gameLobbyById(id);
        var player = new Player(playerName);
        gameLobby.addPlayer(player); // TODO: exception handling
        var sseEmitter = this.emitterService.getEmitterById(id);
        sseEmitter.send(player.getId());
        return sseEmitter;
    }
}
