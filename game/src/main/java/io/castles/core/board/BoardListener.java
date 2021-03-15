package io.castles.core.board;

import io.castles.core.tile.Tile;

import java.util.Map;

public interface BoardListener {

    void currentState(Map<Integer, Map<Integer, Tile>> board);

    void onTileAdded(Tile tile);
}
