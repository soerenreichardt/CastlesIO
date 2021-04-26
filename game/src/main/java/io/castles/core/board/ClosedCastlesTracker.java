package io.castles.core.board;

import io.castles.core.graph.Graph;
import io.castles.core.graph.algorithm.AbstractBreadthFirstSearch;
import io.castles.core.graph.algorithm.GraphBfs;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.core.tile.TileLayout;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClosedCastlesTracker {

    private final Graph graph;
    private final BoardGraph.TileLookup tileLookup;

    public ClosedCastlesTracker(Graph graph, BoardGraph.TileLookup tileLookup) {
        this.graph = graph;
        this.tileLookup = tileLookup;
    }

    public Set<Graph.Node> closedCastleNodes(Tile tile) {
        var tileLayout = tile.<MatrixTileLayout>getTileLayout();
        var contentMatrix = tileLayout.getContent();

        Set<Graph.Node> startPositions = new HashSet<>();
        for (int row = 0; row < contentMatrix.getRows(); row++) {
            for (int column = 0; column < contentMatrix.getColumns(); column++) {
                if (contentMatrix.get(row, column) == TileContent.CASTLE) {
                    startPositions.add(new Graph.Node(tile.getX(), tile.getY(), row, column));
                }
            }
        }

        Set<Graph.Node> seenNodes = new HashSet<>();
        Set<Graph.Node> closedCastleNodes = new HashSet<>();
        var graphBfs = new GraphBfs(graph);
        var nodeVisitor = new NodeVisitor(tileLookup, seenNodes);
        for (Graph.Node startPosition : startPositions) {
            if (seenNodes.contains(startPosition)) {
                continue;
            }
            graphBfs.compute(startPosition, nodeVisitor);
            if (nodeVisitor.castleClosed()) {
                closedCastleNodes.add(startPosition);
            }
        }
        return closedCastleNodes;
    }

    static class NodeVisitor implements AbstractBreadthFirstSearch.BfsVisitor<Graph.Node> {

        private final BoardGraph.TileLookup tileLookup;
        private final Set<Graph.Node> seenNodes;
        private boolean castleClosed;

        NodeVisitor(BoardGraph.TileLookup tileLookup, Set<Graph.Node> seenNodes) {
            this.tileLookup = tileLookup;
            this.seenNodes = seenNodes;
            this.castleClosed = true;
        }

        boolean castleClosed() {
            return castleClosed;
        }

        @Override
        public boolean visit(Graph.Node node, Collection<Graph.Node> neighbors) {
            seenNodes.add(node);

            if (neighbors.size() == TileLayout.NUM_EDGES) {
                return true;
            }

            var tile = tileLookup.resolve(node.getX(), node.getY());

            if (tile.<MatrixTileLayout>getTileLayout().getContent().get(node.getRow(), node.getColumn()) == TileContent.SHARED) {
                return false;
            }

            var contentMatrix = tile.<MatrixTileLayout>getTileLayout().getContent();

            var rows = contentMatrix.getRows();
            var columns = contentMatrix.getColumns();

            var row = node.getRow();
            var column = node.getColumn();

            if (row == 0) {
                if (tile.getNeighbors()[TileLayout.TOP] == null) {
                    this.castleClosed = false;
                    return false;
                }
            }
            if (column == 0) {
                if (tile.getNeighbors()[TileLayout.LEFT] == null) {
                    this.castleClosed = false;
                    return false;
                }
            }
            if (row == rows - 1) {
                if (tile.getNeighbors()[TileLayout.BOTTOM] == null) {
                    this.castleClosed = false;
                    return false;
                }
            }
            if (column == columns - 1) {
                if (tile.getNeighbors()[TileLayout.RIGHT] == null) {
                    this.castleClosed = false;
                    return false;
                }
            }

            return true;
        }
    }
}
