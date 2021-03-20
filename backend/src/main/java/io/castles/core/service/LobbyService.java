package io.castles.core.service;

import io.castles.core.model.LobbyStateDTO;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.NoSuchElementException;
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
        gameLobby.addPlayer(player);
    }

    public SseEmitter reconnectToLobby(UUID id, UUID playerId) {
        var gameLobby = server.gameLobbyById(id);
        if (!gameLobby.containsPlayer(playerId)) {
            throw new NoSuchElementException(String.format("No player with id %s found in lobby %s", playerId, id));
        }
        return emitterService.connectToLobby(id, playerId);
    }

    public LobbyStateDTO getLobbyStateDTOFromGameLobbyForPlayer(GameLobby gameLobby, UUID playerId) {
        LobbyStateDTO lobbyStateDTO = LobbyStateDTO.from(gameLobby);
        if (gameLobby.getOwnerId().equals(playerId)) {
            lobbyStateDTO.getLobbySettings().setEditable(true);
        }
        return lobbyStateDTO;
    }
}
