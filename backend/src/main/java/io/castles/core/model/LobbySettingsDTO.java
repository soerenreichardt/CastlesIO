package io.castles.core.model;

import io.castles.core.GameMode;
import io.castles.core.tile.Tile;
import io.castles.game.GameLobbySettings;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LobbySettingsDTO {
    private final int turnTimeSeconds;
    private final int maxPlayers;
    private final String gameMode;
    private final List<Long> tileList;
    private final List<String> gameModes;

    private boolean editable = false;

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
