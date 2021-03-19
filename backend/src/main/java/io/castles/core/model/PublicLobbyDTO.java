package io.castles.core.model;

import io.castles.game.GameLobby;
import lombok.Value;

@Value
public class PublicLobbyDTO {
    String name;
    String id;
    Number numPlayers;
    Number maxPlayers;

    public static PublicLobbyDTO from(GameLobby lobby) {
        return new PublicLobbyDTO(
                lobby.getName(),
                lobby.getId().toString(),
                lobby.getNumPlayers(),
                lobby.getMaxPlayers()
        );
    }
}
