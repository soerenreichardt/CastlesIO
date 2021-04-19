package io.castles.core.graph.algorithm;

import io.castles.core.graph.Graph;
import io.castles.core.tile.Matrix;
import io.castles.core.tile.TileContent;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class MatrixBfs extends AbstractBreadthFirstSearch<Graph.Node> {

    private final Matrix<TileContent> contentMatrix;
    private final TileContent tileContent;
    private final BitSet seenPositions;
    private final int x;
    private final int y;

    public MatrixBfs(Matrix<TileContent> contentMatrix, TileContent tileContent, BitSet seenPositions, int x, int y) {
        this.contentMatrix = contentMatrix;
        this.tileContent = tileContent;
        this.seenPositions = seenPositions;
        this.x = x;
        this.y = y;
    }

    @Override
    List<Graph.Node> getNeighbors(Graph.Node element) {
        int row = element.getRow();
        int column = element.getColumn();
        List<Graph.Node> neighbors = new ArrayList<>();
        if (row > 0) visitNeighbor(contentMatrix, neighbors, seenPositions, row - 1, column, x, y);
        if (row < contentMatrix.getRows() - 1) visitNeighbor(contentMatrix, neighbors, seenPositions,row + 1, column, x, y);
        if (column > 0) visitNeighbor(contentMatrix, neighbors, seenPositions, row, column - 1, x, y);
        if (column < contentMatrix.getColumns() - 1) visitNeighbor(contentMatrix, neighbors, seenPositions, row, column + 1, x, y);

        return neighbors;
    }

    private void visitNeighbor(Matrix<TileContent> contentMatrix, List<Graph.Node> validNeighbors, BitSet seenPositions, int row, int column, int x, int y) {
        TileContent tileContent = contentMatrix.get(row, column);
        int matrixIndex = column + contentMatrix.getColumns() * row;
        if (tileContent == this.tileContent && !seenPositions.get(matrixIndex)) {
            validNeighbors.add(new Graph.Node(x, y, row, column));
            seenPositions.set(matrixIndex);
        }
    }
}
