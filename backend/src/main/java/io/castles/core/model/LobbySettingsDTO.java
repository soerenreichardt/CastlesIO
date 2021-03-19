package io.castles.core.model;

import io.castles.core.GameMode;
import io.castles.core.Visibility;
import io.castles.core.tile.Tile;
import io.castles.game.GameLobbySettings;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LobbySettingsDTO {
    private final int turnTimeSeconds;
    private final int maxPlayers;
    private final List<Long> tileList;
    private final String gameMode;
    private final List<String> gameModes;
    private final String visibility;
    private final List<String> visibilities;

    private boolean editable = false;

    public GameLobbySettings toGameLobbySettings() {
        return new GameLobbySettings(turnTimeSeconds, maxPlayers, GameMode.valueOf(gameMode), List.of(), Visibility.valueOf(visibility));
    }

    public static LobbySettingsDTO from(GameLobbySettings lobbySettings) {
        return new LobbySettingsDTO(
                lobbySettings.getTurnTimeSeconds(),
                lobbySettings.getMaxPlayers(),
                lobbySettings.getTileList().stream().map(Tile::getId).collect(Collectors.toList()),
                lobbySettings.getGameMode().toString(),
                Stream.of(GameMode.values()).map(Enum::name).collect(Collectors.toList()),
                lobbySettings.getVisibility().toString(),
                Stream.of(Visibility.values()).map(Enum::name).collect(Collectors.toList())
        );
    }
}
