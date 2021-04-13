package io.castles.core.model.dto;

import io.castles.game.Game;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Value
public class GameDTO {
    GameStateDTO gameState;
    Map<Integer, Map<Integer, TileDTO>> tiles;
    List<PlayerDTO> players;

    public static GameDTO from(Game game) {
        return new GameDTO(
                GameStateDTO.from(game),
                game.getGameBoardTileMap().entrySet().stream().collect(
                        Collectors.toMap(Entry::getKey, xVal -> xVal.getValue().entrySet().stream().collect(
                                Collectors.toMap(Entry::getKey, yVal -> TileDTO.from(yVal.getValue()))
                        ))
                ),
                game.getPlayers().stream().map(PlayerDTO::from).collect(Collectors.toList())
        );
    }
}
