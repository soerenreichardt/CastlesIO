package io.castles.game;

import io.castles.core.GameMode;
import io.castles.core.tile.Tile;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
@Builder
@Getter
public class GameSettings {
    GameMode gameMode;
    String name;
    @Builder.Default
    Optional<List<Tile>> tileList = Optional.empty();
}
