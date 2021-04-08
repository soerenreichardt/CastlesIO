package io.castles.core.model.dto;

import io.castles.game.GameLobby;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class LobbyStateDTO {
    List<PlayerDTO> players;
    String lobbyName;
    LobbySettingsDTO lobbySettings;

    public static LobbyStateDTO from(GameLobby gameLobby) {
        return new LobbyStateDTO(
                gameLobby.getPlayers().stream().map(PlayerDTO::from).collect(Collectors.toList()),
                gameLobby.getName(),
                LobbySettingsDTO.from(gameLobby.getLobbySettings())
        );
    }
}