package io.castles.core.board.statistics.algorithm;

import io.castles.core.board.statistics.Graph;
import io.castles.core.tile.Matrix;
import io.castles.core.tile.TileContent;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class MatrixBfs extends AbstractBreadthFirstSearch<Graph.Node> {

    private final Matrix<TileContent> contentMatrix;
    private final TileContent tileContent;
    private final long tileId;
    private final BitSet seenPositions;

    public MatrixBfs(Matrix<TileContent> contentMatrix, TileContent tileContent, BitSet seenPositions, long tileId) {
        this.contentMatrix = contentMatrix;
        this.tileContent = tileContent;
        this.tileId = tileId;
        this.seenPositions = seenPositions;
    }

    @Override
    List<Graph.Node> getNeighbors(Graph.Node element) {
        int row = element.getRow();
        int column = element.getColumn();
        List<Graph.Node> neighbors = new ArrayList<>();
        if (row > 0) visitNeighbor(contentMatrix, neighbors, seenPositions, row - 1, column, tileId);
        if (row < contentMatrix.getRows() - 1) visitNeighbor(contentMatrix, neighbors, seenPositions,row + 1, column, tileId);
        if (column > 0) visitNeighbor(contentMatrix, neighbors, seenPositions, row, column - 1, tileId);
        if (column < contentMatrix.getColumns() - 1) visitNeighbor(contentMatrix, neighbors, seenPositions, row, column + 1, tileId);

        return neighbors;
    }

    private void visitNeighbor(Matrix<TileContent> contentMatrix, List<Graph.Node> validNeighbors, BitSet seenPositions, int row, int column, long tileId) {
        TileContent tileContent = contentMatrix.get(row, column);
        int matrixIndex = column + contentMatrix.getColumns() * row;
        if (tileContent == this.tileContent && !seenPositions.get(matrixIndex)) {
            validNeighbors.add(new Graph.Node(tileId, row, column));
            seenPositions.set(matrixIndex);
        }
    }
}
