package io.castles.core.model;

import io.castles.game.GameLobby;
import lombok.Value;

@Value
public class PublicLobbyDTO {
    String name;
    Number numPlayers;
    Number maxPlayers;

    public static PublicLobbyDTO from(GameLobby lobby) {
        return new PublicLobbyDTO(
                lobby.getName(),
                lobby.getNumPlayers(),
                lobby.getMaxPlayers()
        );
    }
}
