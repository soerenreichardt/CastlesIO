package io.castles.core.model.dto;

import io.castles.core.GameMode;
import io.castles.game.GameSettings;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor
public class GameSettingsDTO {
    int turnTimeSeconds;
    int maxPlayers;
    GameMode gameMode;
    List<TileDTO> tileList;

    public static GameSettingsDTO from(GameSettings settings) {
        return new GameSettingsDTO(
                settings.getTurnTimeSeconds(),
                settings.getMaxPlayers(),
                settings.getGameMode(),
                settings.getTileList().stream().map(TileDTO::from).collect(Collectors.toList())
        );
    }
}
