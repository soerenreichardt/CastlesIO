package io.castles.core.service;

import io.castles.core.GameMode;
import io.castles.core.Visibility;
import io.castles.core.model.LobbySettingsDTO;
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
        gameLobby.addPlayer(player); // TODO: exception handling

        var playerId = player.getId();
        emitterService.createPlayerEmitterForLobby(id, playerId);

        updateLobbyState(gameLobby.getId());
    }

    public SseEmitter reconnectLobby(UUID id, UUID playerId) {
        var gameLobby = server.gameLobbyById(id);
        if (!gameLobby.containsPlayer(playerId)) {
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

    public LobbyStateDTO getLobbyStateDTOFromGameLobbyForPlayer(GameLobby gameLobby, UUID playerId) {
        LobbyStateDTO lobbyStateDTO = LobbyStateDTO.from(gameLobby);
        if (gameLobby.getOwnerId().equals(playerId)) {
            lobbyStateDTO.getLobbySettings().setEditable(true);
        }
        return lobbyStateDTO;
    }

    public void updateLobbySettings(GameLobby gameLobby, LobbySettingsDTO lobbySettingsDTO) {
        var lobbySettings = gameLobby.getLobbySettings();
        lobbySettings.setTurnTimeSeconds(lobbySettingsDTO.getTurnTimeSeconds());
        lobbySettings.setMaxPlayers(lobbySettingsDTO.getMaxPlayers());
        lobbySettings.setGameMode(GameMode.valueOf(lobbySettingsDTO.getGameMode()));
        lobbySettings.setVisibility(Visibility.valueOf(lobbySettingsDTO.getVisibility()));

        updateLobbyState(gameLobby.getId());
    }
}
