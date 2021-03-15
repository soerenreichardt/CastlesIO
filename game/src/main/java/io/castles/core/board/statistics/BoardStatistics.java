package io.castles.core.board.statistics;

import io.castles.core.board.BoardListener;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;

import java.util.List;
import java.util.Map;

public class BoardStatistics implements BoardListener {

    public BoardStatistics() {
    }

    @Override
    public void onTileAdded(Tile tile) {
    }

    public Graph getGraphByTileContent(TileContent tileContent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void currentState(Map<Integer, Map<Integer, Tile>> board) {

    }
}
