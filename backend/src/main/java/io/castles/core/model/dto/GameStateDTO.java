package io.castles.core.model.dto;

import io.castles.game.GameState;
import io.castles.game.Player;
import lombok.Value;

@Value
public class GameStateDTO {
    GameState gameState;
    Player player;
}
