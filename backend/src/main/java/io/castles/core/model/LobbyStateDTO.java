package io.castles.core.model;

import io.castles.game.GameLobby;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class LobbyStateDTO {
    List<String> playerNames;
    String lobbyName;
    LobbySettingsDTO lobbySettings;

    public static LobbyStateDTO from(GameLobby gameLobby) {
        return new LobbyStateDTO(
                gameLobby.getPlayerNames(),
                gameLobby.getName(),
                LobbySettingsDTO.from(gameLobby.getLobbySettings())
        );
    }
}
