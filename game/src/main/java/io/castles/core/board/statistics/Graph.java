package io.castles.core.board.statistics;

import io.castles.core.tile.Matrix;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Graph {

    private final TileContent tileContent;

    private final Set<Node> nodes;
    private final Map<Node, Set<Node>> relationships;

    public Graph(TileContent tileContent) {
        this.tileContent = tileContent;
        this.nodes = new HashSet<>();
        this.relationships = new HashMap<>();
    }

    public Set<Node> nodes() {
        return nodes;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void addRelationship(Relationship relationship) {
        Node source = relationship.source;
        Node target = relationship.target;
        if (!nodes.contains(source)) addNode(source);
        if (!nodes.contains(target)) addNode(target);

        this.relationships.computeIfAbsent(source, __ -> new HashSet<>()).add(target);
        this.relationships.computeIfAbsent(target, __ -> new HashSet<>()).add(source);
    }

    public void fromTile(Tile tile) {
        createTileInternalGraph(tile);
    }

    private void createTileInternalGraph(Tile tile) {
        MatrixTileLayout tileLayout = tile.getTileLayout();
        Matrix<TileContent> contentMatrix = tileLayout.getContent();
        int rows = contentMatrix.getRows();
        int columns = contentMatrix.getColumns();

        BitSet seenPositions = new BitSet(rows * columns);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int matrixIndex = column + columns * row;
                if (!seenPositions.get(matrixIndex)) {
                    if (contentMatrix.get(row, column) == tileContent) {
                        seenPositions.set(matrixIndex);
                        graphFromMatrixBfs(contentMatrix, seenPositions, row, column, tile.getId());
                    }
                }
            }
        }
    }

    private void graphFromMatrixBfs(Matrix<TileContent> contentMatrix, BitSet seenPositions, int row, int column, long tileId) {
        Set<Node> bfsSeen = new HashSet<>();
        Queue<Node> queue = new LinkedBlockingQueue<>();

        Node node = new Node(tileId, row, column);
        queue.offer(node);
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            if (bfsSeen.contains(currentNode))  continue;

            List<Node> neighbors = new ArrayList<>();
            if (row > 0) visitNeighbor(contentMatrix, neighbors, seenPositions, row - 1, column, tileId);
            if (row < contentMatrix.getRows() - 1) visitNeighbor(contentMatrix, neighbors, seenPositions,row + 1, column, tileId);
            if (column > 0) visitNeighbor(contentMatrix, neighbors, seenPositions, row, column - 1, tileId);
            if (column < contentMatrix.getColumns() - 1) visitNeighbor(contentMatrix, neighbors, seenPositions, row, column + 1, tileId);

            addNode(currentNode);
            bfsSeen.add(currentNode);
            neighbors.forEach(queue::offer);
        }
    }

    private void visitNeighbor(Matrix<TileContent> contentMatrix, List<Node> validNeighbors, BitSet seenPositions, int row, int column, long tileId) {
        TileContent tileContent = contentMatrix.get(row, column);
        seenPositions.set(column + contentMatrix.getColumns() * row);
        if (tileContent == this.tileContent) {
            validNeighbors.add(new Node(tileId, row, column));
        }
    }

    @Value
    @EqualsAndHashCode
    static class Position {
        int row;
        int column;
    }

    @Value
    static class Node {
        long tileId;
        int row;
        int column;
    }

    @Value
    @EqualsAndHashCode
    static class Relationship {
        Node source;
        Node target;
    }
}
