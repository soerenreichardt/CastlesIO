package io.castles.game;

import io.castles.core.GameMode;
import org.immutables.value.Value;

@Value.Immutable
public interface GameSettings {
    GameMode gameMode();

    String name();
}
