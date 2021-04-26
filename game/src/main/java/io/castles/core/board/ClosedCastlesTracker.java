package io.castles.core.board;

import io.castles.core.graph.Graph;
import io.castles.core.graph.algorithm.AbstractBreadthFirstSearch;
import io.castles.core.graph.algorithm.GraphBfs;
import io.castles.core.tile.*;

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

        // Find all matrix elements of type CASTLE.
        // They will be treated as start positions for the closed castles search.
        // This is necessary, as one tile can have multiple disconnected castles.
        Set<Graph.Node> startPositions = getCastleFieldsOfTile(tile);

        Set<Graph.Node> seenNodes = new HashSet<>();
        Set<Graph.Node> closedCastleNodes = new HashSet<>();
        var graphBfs = new GraphBfs(graph);
        var nodeVisitor = new ClosedCastleNodeVisitor(tileLookup, seenNodes);
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

    private Set<Graph.Node> getCastleFieldsOfTile(Tile tile) {
        Set<Graph.Node> startPositions = new HashSet<>();
        Matrix<TileContent> contentMatrix = tile.<MatrixTileLayout>getTileLayout().getContent();
        for (int row = 0; row < contentMatrix.getRows(); row++) {
            for (int column = 0; column < contentMatrix.getColumns(); column++) {
                if (contentMatrix.get(row, column) == TileContent.CASTLE) {
                    startPositions.add(new Graph.Node(tile.getX(), tile.getY(), row, column));
                }
            }
        }
        return startPositions;
    }

    /**
     * Visits nodes of a BFS traversal and computes whether a castle is closed or not.
     * This is done by checking for every castle node if it marks the end of the board so far.
     * A castle is considered NOT closed, iff one or more of the castle nodes in the graph have
     * less than 4 neighbors and the board doesn't continue in the direction of all of the missing neighbors.
     */
    static class ClosedCastleNodeVisitor implements AbstractBreadthFirstSearch.BfsVisitor<Graph.Node> {

        private final BoardGraph.TileLookup tileLookup;
        private final Set<Graph.Node> seenNodes;
        private boolean castleClosed;

        ClosedCastleNodeVisitor(BoardGraph.TileLookup tileLookup, Set<Graph.Node> seenNodes) {
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
                return true;
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
