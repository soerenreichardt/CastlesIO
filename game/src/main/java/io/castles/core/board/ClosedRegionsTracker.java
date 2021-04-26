package io.castles.core.board;

import io.castles.core.graph.Graph;
import io.castles.core.graph.algorithm.AbstractBreadthFirstSearch;
import io.castles.core.graph.algorithm.GraphBfs;
import io.castles.core.tile.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClosedRegionsTracker {

    private final Graph graph;
    private final BoardGraph.TileLookup tileLookup;

    public ClosedRegionsTracker(Graph graph, BoardGraph.TileLookup tileLookup) {
        this.graph = graph;
        this.tileLookup = tileLookup;
    }

    public Set<Graph.Node> closedRegionNodes(Tile tile) {

        // Find all matrix elements of the TileContent the corresponding graph supports.
        // They will be treated as start positions for the closed regions search.
        // This is necessary, as one tile can have multiple disconnected regions of a specific TileContent.
        Set<Graph.Node> startPositions = filterFieldsOfRegionTypeInTile(tile);

        Set<Graph.Node> seenNodes = new HashSet<>();
        Set<Graph.Node> closedRegionNodes = new HashSet<>();
        var graphBfs = new GraphBfs(graph);
        var nodeVisitor = new ClosedRegionNodeVisitor(tileLookup, seenNodes);
        for (Graph.Node startPosition : startPositions) {
            if (seenNodes.contains(startPosition)) {
                continue;
            }
            graphBfs.compute(startPosition, nodeVisitor);
            if (nodeVisitor.regionClosed()) {
                closedRegionNodes.add(startPosition);
            }
        }
        return closedRegionNodes;
    }

    private Set<Graph.Node> filterFieldsOfRegionTypeInTile(Tile tile) {
        Set<Graph.Node> startPositions = new HashSet<>();
        Matrix<TileContent> contentMatrix = tile.<MatrixTileLayout>getTileLayout().getContent();
        for (int row = 0; row < contentMatrix.getRows(); row++) {
            for (int column = 0; column < contentMatrix.getColumns(); column++) {
                if (contentMatrix.get(row, column) == graph.tileContent()) {
                    startPositions.add(new Graph.Node(tile.getX(), tile.getY(), row, column));
                }
            }
        }
        return startPositions;
    }

    /**
     * Visits nodes of a BFS traversal and computes whether a region is closed or not.
     * This is done by checking for every node of a specific TileContent if it marks the end of the board so far.
     * A region is considered NOT closed, iff one or more of the region nodes in the graph have
     * less than 4 neighbors and the board doesn't continue in the direction of all of the missing neighbors.
     */
    static class ClosedRegionNodeVisitor implements AbstractBreadthFirstSearch.BfsVisitor<Graph.Node> {

        private final BoardGraph.TileLookup tileLookup;
        private final Set<Graph.Node> seenNodes;
        private boolean regionClosed;

        ClosedRegionNodeVisitor(BoardGraph.TileLookup tileLookup, Set<Graph.Node> seenNodes) {
            this.tileLookup = tileLookup;
            this.seenNodes = seenNodes;
            this.regionClosed = true;
        }

        boolean regionClosed() {
            return regionClosed;
        }

        @Override
        public boolean visit(Graph.Node node, Collection<Graph.Node> neighbors) {
            seenNodes.add(node);

            if (neighbors.size() == TileLayout.NUM_EDGES) {
                return true;
            }

            var tile = tileLookup.resolve(node.getX(), node.getY());

            var tileLayout = tile.<MatrixTileLayout>getTileLayout();
            if (tileLayout.getContent().get(node.getRow(), node.getColumn()) == TileContent.SHARED) {
                return true;
            }

            var contentMatrix = tileLayout.getContent();

            var rows = contentMatrix.getRows();
            var columns = contentMatrix.getColumns();

            var row = node.getRow();
            var column = node.getColumn();

            var activeRotation = tileLayout.getActiveRotation();
            if (row == 0) {
                if (tile.getNeighbors()[activeRotation[TileLayout.TOP]] == null) {
                    this.regionClosed = false;
                    return false;
                }
            }
            if (column == 0) {
                if (tile.getNeighbors()[activeRotation[TileLayout.LEFT]] == null) {
                    this.regionClosed = false;
                    return false;
                }
            }
            if (row == rows - 1) {
                if (tile.getNeighbors()[activeRotation[TileLayout.BOTTOM]] == null) {
                    this.regionClosed = false;
                    return false;
                }
            }
            if (column == columns - 1) {
                if (tile.getNeighbors()[activeRotation[TileLayout.RIGHT]] == null) {
                    this.regionClosed = false;
                    return false;
                }
            }

            return true;
        }
    }
}
