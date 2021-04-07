package io.castles.core.model.dto;

import io.castles.game.Game;
import lombok.Value;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
public class GameStartDTO {
    UUID gameId;
    List<PlayerDTO> players;
    PlayerDTO startingPlayer;
    GameSettingsDTO settings;
    TileDTO startTile;

    public static GameStartDTO from(Game game) {
        return new GameStartDTO(
                game.getId(),
                game.getPlayers().stream().map(PlayerDTO::from).collect(Collectors.toList()),
                PlayerDTO.from(game.getActivePlayer()),
                GameSettingsDTO.from(game.getSettings()),
                TileDTO.from(game.getStartTile())
        );
    }
}
