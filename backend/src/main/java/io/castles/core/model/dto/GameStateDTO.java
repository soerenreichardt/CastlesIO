package io.castles.core.model.dto;

import io.castles.game.GameState;
import lombok.Value;

@Value
public class GameStateDTO {
    GameState gameState;
    PlayerDTO player;
}
