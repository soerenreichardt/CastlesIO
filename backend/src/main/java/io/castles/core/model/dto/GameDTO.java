package io.castles.core.model.dto;

import io.castles.game.Game;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
public class GameDTO {
    String name;
    GameStateDTO gameState;
    Map<Integer, Map<Integer, TileDTO>> tiles;
    List<PlayerDTO> players;
    Map<UUID, Integer> playerFiguresLeft;
    int tilesLeft;

    public static GameDTO from(Game game) {
        var board = game.getGameBoardTileMap().entrySet().stream().collect(Collectors.toMap(
                Entry::getKey,
                xVal -> xVal.getValue().entrySet().stream().collect(Collectors.toMap(
                        Entry::getKey,
                        yVal -> TileDTO.from(yVal.getValue()))
                ))
        );

        var playerFiguresLeft = game.getFiguresLeft().entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().getId(),
                Entry::getValue
        ));

        return new GameDTO(
                game.getName(),
                GameStateDTO.from(game),
                board,
                game.getPlayers().stream().map(PlayerDTO::from).collect(Collectors.toList()),
                playerFiguresLeft,
                game.getGameBoardNumTilesLeft()
        );
    }
}
