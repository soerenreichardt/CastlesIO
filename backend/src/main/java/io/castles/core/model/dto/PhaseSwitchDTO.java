package io.castles.core.model.dto;

import io.castles.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PhaseSwitchDTO {
    GameState from;
    GameState to;
}
