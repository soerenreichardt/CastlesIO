package io.castles.game;

import io.castles.core.GameMode;
import io.castles.core.tile.Tile;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class GameSettings {
    int turnTimeSeconds;
    int maxPlayers;
    GameMode gameMode;
    List<Tile> tileList;

    public static GameSettings from(GameLobbySettings lobbySettings) {
        return new GameSettings(
                lobbySettings.getTurnTimeSeconds(),
                lobbySettings.getMaxPlayers(),
                lobbySettings.getGameMode(),
                lobbySettings.getTileList()
        );
    }
}
