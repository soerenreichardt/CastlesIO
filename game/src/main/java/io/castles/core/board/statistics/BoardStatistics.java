package io.castles.core.board.statistics;

import io.castles.core.board.BoardListener;
import io.castles.core.graph.Graph;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoardStatistics implements BoardListener {

    private final List<Graph> graphs;

    public BoardStatistics() {
        this.graphs = List.of(
                new Graph(TileContent.GRAS),
                new Graph(TileContent.CASTLE),
                new Graph(TileContent.STREET)
        );
    }

    @Override
    public void onTileAdded(Tile tile) {
        graphs.forEach(graph -> graph.fromTile(tile));
    }

    @Override
    public void currentState(Map<Integer, Map<Integer, Tile>> board) {
        List<Tile> tiles = board.entrySet()
                .stream()
                .flatMap(inner -> inner.getValue().entrySet().stream())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        graphs.forEach(graph -> graph.fromExistingBoard(tiles));
    }
}
