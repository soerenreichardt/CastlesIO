package io.castles.core.graph;

import io.castles.core.graph.algorithm.MatrixBfs;
import io.castles.core.tile.*;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.*;
import java.util.function.BiConsumer;

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

    public TileContent tileContent() {
        return tileContent;
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
        connectTileToAdjacentGraph(tile);
    }

    public void fromExistingBoard(List<Tile> tiles) {
        tiles.forEach(this::createTileInternalGraph);
        tiles.forEach(this::connectTileToAdjacentGraph);
    }

    public void forEachRelationship(BiConsumer<Graph.Node, Graph.Node> relationshipVisitor) {
        this.relationships.forEach((source, targets) -> {
            targets.forEach(target -> relationshipVisitor.accept(source, target));
        });
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
            return true;
        });
    }

    private void connectTileToAdjacentGraph(Tile tile) {
        Tile[] neighbors = tile.getNeighbors();
        for (int direction = 0, neighborsLength = neighbors.length; direction < neighborsLength; direction++) {
            Tile neighbor = neighbors[direction];
            if (neighbor != null) {
                connectTileToAdjacentTile(tile, neighbor, direction);
            }
        }
    }

    /**
     * Creates relationships between touching edges of
     * adjacent tiles.
     */
    private void connectTileToAdjacentTile(Tile tile, Tile neighbor, int direction) {
        MatrixTileLayout tileLayout = tile.getTileLayout();
        MatrixTileLayout neighborLayout = neighbor.getTileLayout();

        var tileContentEdge = tileLayout.getTileContentEdge(direction);
        var neighborContentEdge = neighborLayout.getTileContentEdge(TileSupport.oppositeDirection(direction));

        // Gets mapping information between indices of 2 adjacent tile edges
        // Positions in smaller edges are mapped to one or more positions in larger edges
        Map<Integer, List<Integer>> tileContentEdgeIndicesMapping = new HashMap<>();
        MatrixTileLayout.compareTileEdgesWithIndex(tileContentEdge, neighborContentEdge, (lhsEdge, lhsIndex, rhsEdge, rhsIndex) -> {
            if (lhsEdge == tileContent && rhsEdge == tileContent) {
                tileContentEdgeIndicesMapping.computeIfAbsent(lhsIndex, __ -> new ArrayList<>()).add(rhsIndex);
            }
            return true;
        });

        // Find larger and smaller tile edge
        Tile smallTile;
        Tile largeTile;
        int smallDirection;
        int largeDirection;

        if (tileContentEdge.length >= neighborContentEdge.length) {
            largeTile = tile;
            largeDirection = direction;
            smallTile = neighbor;
            smallDirection = TileSupport.oppositeDirection(direction);
        } else {
            largeTile = neighbor;
            largeDirection = TileSupport.oppositeDirection(direction);
            smallTile = tile;
            smallDirection = direction;
        }

        traverseEdgeIndicesMapping(tileContentEdgeIndicesMapping, smallTile, largeTile, smallDirection, largeDirection);
    }

    /**
     * Traverse index mapping information between smaller and larger edge
     * Creates a relationship between any of these mappings
     */
    private void traverseEdgeIndicesMapping(Map<Integer, List<Integer>> tileContentEdgeIndicesMapping, Tile smallTile, Tile largeTile, int smallDirection, int largeDirection) {
        tileContentEdgeIndicesMapping.forEach((smallIndex, largeIndices) -> {
            MatrixTileLayout smallLayout = smallTile.getTileLayout();
            Matrix<TileContent> smallMatrix = smallLayout.getContent();
            int resolvedSmallMatrixIndex = smallLayout.getResolvedPositionInMatrix(smallIndex, smallDirection);
            Node source = new Node(smallTile.getId(), smallMatrix.getRowFromIndex(resolvedSmallMatrixIndex), smallMatrix.getColumnFromIndex(resolvedSmallMatrixIndex));

            largeIndices.forEach(largeIndex -> {
                MatrixTileLayout largeLayout = largeTile.getTileLayout();
                Matrix<TileContent> largeMatrix = largeLayout.getContent();
                int resolvedLargeMatrixIndex = largeLayout.getResolvedPositionInMatrix(largeIndex, largeDirection);
                Node target = new Node(largeTile.getId(), largeMatrix.getRowFromIndex(resolvedLargeMatrixIndex), largeMatrix.getColumnFromIndex(resolvedLargeMatrixIndex));
                if (!nodes.contains(source) || !nodes.contains(target)) {
                    throw new IllegalStateException("Computed node not found in list of nodes");
                }
                addRelationship(source, target);
            });
        });
    }

    @Value
    @EqualsAndHashCode
    public static class Node {
        long tileId;
        int row;
        int column;
    }
}
