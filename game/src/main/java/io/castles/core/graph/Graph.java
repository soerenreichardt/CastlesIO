package io.castles.core.graph;

import io.castles.core.graph.algorithm.MatrixBfs;
import io.castles.core.tile.Matrix;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import lombok.Value;

import java.util.*;

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

    public int nodeCount() {
        return nodes.size();
    }

    public Map<Node, Set<Node>> relationships() {
        return relationships;
    }

    public int relationshipCount() {
        return (int) relationships.entrySet().stream().flatMap(entry -> entry.getValue().stream()).count();
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void addRelationship(Node source, Node target) {
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
        MatrixBfs matrixBfs = new MatrixBfs(contentMatrix, tileContent, seenPositions, tileId);
        matrixBfs.compute(new Node(tileId, row, column), (node, neighbors) -> {
            addNode(node);
            neighbors.forEach(neighbor -> addRelationship(node, neighbor));
        });
    }

    @Value
    public static class Node {
        long tileId;
        int row;
        int column;
    }
}
