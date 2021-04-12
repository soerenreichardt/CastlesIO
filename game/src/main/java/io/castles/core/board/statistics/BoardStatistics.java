package io.castles.core.board.statistics;

import io.castles.core.board.BoardListener;
import io.castles.core.graph.Graph;
import io.castles.core.graph.algorithm.GraphBfs;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;

import java.util.*;
import java.util.stream.Collectors;

public class BoardStatistics implements BoardListener {

    private final List<Graph> graphs;

    public BoardStatistics() {
        this.graphs = new ArrayList<>();
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

    @Override
    public void initialize() {
        graphs.add(new Graph(TileContent.GRAS));
        graphs.add(new Graph(TileContent.CASTLE));
        graphs.add(new Graph(TileContent.STREET));
    }

    @Override
    public void restart() {
        graphs.clear();
        initialize();
    }

    public int getStreetLength(Tile tile, int row, int column) {
        Graph graph = graphs.stream()
                .filter(g -> g.tileContent() == TileContent.STREET)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No graph was found for TileContent %s", TileContent.STREET)));

        GraphBfs graphBfs = new GraphBfs(graph);
        Graph.Node startNode = new Graph.Node(tile.getId(), row, column);
        if (!graph.nodes().contains(startNode)) {
            throw new IllegalArgumentException(String.format("No node %s was found in graph.", startNode));
        }
        Set<Long> distinctTileIds = new HashSet<>();
        graphBfs.compute(startNode, (node, neighbors) -> {
            distinctTileIds.add(node.getTileId());
            // TODO: detect street ends
        });
        return distinctTileIds.size();
    }
}
