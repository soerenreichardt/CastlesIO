package io.castles.game;

import io.castles.core.GameMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder
@Getter
public class GameSettings {
    GameMode gameMode;
    String name;
}
