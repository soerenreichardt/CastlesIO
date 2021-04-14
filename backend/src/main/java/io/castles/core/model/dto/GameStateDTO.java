package io.castles.core.model.dto;

import io.castles.game.Game;
import io.castles.game.GameState;
import lombok.Value;

@Value
public class GameStateDTO {
    GameState state;
    PlayerDTO player;

    public static GameStateDTO from(Game game) {
        return new GameStateDTO(
                game.getCurrentGameState(),
                PlayerDTO.from(game.getActivePlayer())
        );
    }
}
