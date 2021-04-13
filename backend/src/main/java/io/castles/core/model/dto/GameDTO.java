package io.castles.core.model.dto;

import io.castles.core.tile.Tile;
import io.castles.game.Game;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Value
public class GameDTO {
    GameStateDTO gameState;
    Map<Integer, Map<Integer, Tile>> tiles;
    List<PlayerDTO> players;

    public static GameDTO from(Game game) {
        return new GameDTO(
                GameStateDTO.from(game),
                game.getGameBoardTileMap(),
                game.getPlayers().stream().map(PlayerDTO::from).collect(Collectors.toList())
        );
    }
}
