package io.castles.core.service;

import io.castles.core.model.LobbyStateDTO;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        gameLobby.addPlayer(player); // TODO: exception handling

        var playerId = player.getId();
        emitterService.createPlayerEmitterForLobby(id, playerId);

        updateLobbyState(gameLobby.getId());
    }

    public SseEmitter reconnectLobby(UUID id, UUID playerId) {
        var gameLobby = server.gameLobbyById(id);
        if (!gameLobby.isPlayerInLobby(playerId)) {
            throw new NoSuchElementException(String.format("No player with id %s found in lobby %s", playerId, id));
        }
        this.emitterService.createPlayerEmitterForLobby(id, playerId);
        return this.emitterService.getLobbyEmitterForPlayer(id, playerId);
    }

    public void updateLobbyState(UUID id) {
        var gameLobby = server.gameLobbyById(id);
        var playerIds = gameLobby.getPlayerIds();
        for (var playerId : playerIds) {
                updateLobbyStateToPlayer(id, playerId);
        }
    }

    public void updateLobbyStateToPlayer(UUID id, UUID playerId) {
        var gameLobby = server.gameLobbyById(id);
        var playerLobbyStateDTO = getLobbyStateDTOFromGameLobbyForPlayer(gameLobby, playerId);
        var playerSseEmitter = this.emitterService.getLobbyEmitterForPlayer(gameLobby.getId(), playerId);

        try {
            playerSseEmitter.send(playerLobbyStateDTO);
        } catch (IOException e) {
            playerSseEmitter.complete();
        }
    }

    private LobbyStateDTO getLobbyStateDTOFromGameLobbyForPlayer(GameLobby gameLobby, UUID playerId) {
        LobbyStateDTO lobbyStateDTO = LobbyStateDTO.from(gameLobby);
        if (gameLobby.getOwnerId() == playerId) {
            lobbyStateDTO.getLobbySettings().setEditable(true);
        }
        return lobbyStateDTO;
    }
}
