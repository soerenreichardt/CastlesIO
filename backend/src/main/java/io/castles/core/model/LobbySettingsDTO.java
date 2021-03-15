package io.castles.core.model;

import io.castles.core.GameMode;
import io.castles.core.tile.Tile;
import io.castles.game.GameLobbySettings;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
public class LobbySettingsDTO {
    int turnTimeSeconds;
    int maxPlayers;
    String gameMode;
    List<Long> tileList;
    List<String> gameModes;

    public static LobbySettingsDTO from(GameLobbySettings lobbySettings) {
        return new LobbySettingsDTO(
                lobbySettings.getTurnTimeSeconds(),
                lobbySettings.getMaxPlayers(),
                lobbySettings.getGameMode().toString(),
                lobbySettings.getTileList().stream().map(Tile::getId).collect(Collectors.toList()),
                Stream.of(GameMode.values()).map(Enum::name).collect(Collectors.toList())
        );
    }
}
