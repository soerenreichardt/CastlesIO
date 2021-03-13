package io.castles.core.model;

import io.castles.game.GameLobby;
import lombok.Value;

import java.util.List;

@Value
public class LobbyStateDTO {
    List<String> playerNames;
    String lobbyName;

    public static LobbyStateDTO from(GameLobby gameLobby) {
        return new LobbyStateDTO(
                gameLobby.getPlayerNames(),
                gameLobby.getName()
        );
    }
}
