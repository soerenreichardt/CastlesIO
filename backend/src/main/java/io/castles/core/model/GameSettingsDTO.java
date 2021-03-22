package io.castles.core.model;

import io.castles.core.tile.Tile;
import io.castles.game.GameSettings;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSettingsDTO {
    int turnTimeSeconds;
    int maxPlayers;
    List<Long> tileList;
    String gameMode;

    public static GameSettingsDTO from(GameSettings settings) {
        return new GameSettingsDTO(
                settings.getTurnTimeSeconds(),
                settings.getMaxPlayers(),
                settings.getTileList().stream().map(Tile::getId).collect(Collectors.toList()),
                settings.getGameMode().name()
        );
    }
}

