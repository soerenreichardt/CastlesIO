package io.castles.game;

import io.castles.core.GameMode;
import io.castles.core.Visibility;
import io.castles.core.tile.Tile;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class GameLobbySettings {
    @Builder.Default
    private int turnTimeSeconds = 40;
    @Builder.Default
    private int maxPlayers = 5;
    @Builder.Default
    private GameMode gameMode = GameMode.ORIGINAL;
    @Builder.Default
    private List<Tile> tileList = List.of();
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;
}
