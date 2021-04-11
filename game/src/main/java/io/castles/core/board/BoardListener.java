package io.castles.core.board;

import io.castles.core.tile.Tile;
import io.castles.game.Lifecycle;

import java.util.Map;

public interface BoardListener extends Lifecycle {

    void currentState(Map<Integer, Map<Integer, Tile>> board);

    void onTileAdded(Tile tile);
}
