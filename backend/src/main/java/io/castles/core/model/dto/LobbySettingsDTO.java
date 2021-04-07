package io.castles.core.model.dto;

import io.castles.core.GameMode;
import io.castles.core.Visibility;
import io.castles.core.tile.Tile;
import io.castles.game.GameLobbySettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbySettingsDTO {
    int turnTimeSeconds;
    int maxPlayers;
    List<Long> tileList;
    String gameMode;
    List<String> gameModes;
    String visibility;
    List<String> visibilities;

    private boolean editable;

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
                Stream.of(Visibility.values()).map(Enum::name).collect(Collectors.toList()),
                false
        );
    }
}

