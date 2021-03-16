package io.castles.core.tile;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class MatrixTileLayout extends AbstractTileLayout {

    static final int DEFAULT_DIMENSIONS = 3;

    private final Matrix<TileContent> contentMatrix;

    public static MatrixTileLayout createWithRotation(Matrix<TileContent> contentMatrix, int rotation) {
        MatrixTileLayout matrixTileLayout = new MatrixTileLayout(contentMatrix);
        matrixTileLayout.rotate(rotation);
        return matrixTileLayout;
    }

    public MatrixTileLayout(Matrix<TileContent> contentMatrix) {
        assert contentMatrix.getColumns() % 2 == 1;
        assert contentMatrix.getRows() % 2 == 1;

        this.contentMatrix = contentMatrix;
    }

    @Override
    protected boolean matchesTileWithAppliedRotation(TileContent[] otherTileEdge, int rotatedDirection) {
        TileContent[] tileEdge = getTileContentEdgeWithAppliedRotation(rotatedDirection);
        return compareTileEdges(tileEdge, otherTileEdge, TileContent::matches);
    }

    @Override
    protected TileContent[] getTileContentEdgeWithAppliedRotation(int rotatedDirection) {
        int edgeLength = rotatedDirection == LEFT || rotatedDirection == RIGHT
                ? contentMatrix.getRows()
                : contentMatrix.getColumns();
        TileContent[] edge = new TileContent[edgeLength];

        if (rotatedDirection == LEFT) {
            for (int i = 0; i < edgeLength; i++) {
                edge[i] = getResolvedContentAt(i, 0);
            }
        }
        if (rotatedDirection == RIGHT) {
            for (int i = 0; i < edgeLength; i++) {
                edge[i] = getResolvedContentAt(i, contentMatrix.getColumns() - 1);
            }
        }
        if (rotatedDirection == TOP) {
            for (int i = 0; i < edgeLength; i++) {
                edge[i] = getResolvedContentAt(0, i);
            }
        }
        if (rotatedDirection == BOTTOM) {
            for (int i = 0; i < edgeLength; i++) {
                edge[i] = getResolvedContentAt(contentMatrix.getRows() - 1, i);
            }
        }

        return edge;
    }

    @Override
    public TileContent getCenter() {
        int centerRowIndex = (contentMatrix.getRows() / 2);
        int centerColumnIndex = (contentMatrix.getColumns() / 2);
        return getResolvedContentAt(centerRowIndex, centerColumnIndex);
    }

    @Override
    public Matrix<TileContent> getContent() {
        return contentMatrix;
    }

    public int getResolvedPositionInMatrix(int indexInContentEdge, int direction) {
        direction = activeRotation[direction];
        if (direction == TOP) {
            return indexInContentEdge;
        }
        if (direction == BOTTOM) {
            return (contentMatrix.getRows() - 1) * contentMatrix.getColumns() + indexInContentEdge;
        }
        if (direction == LEFT) {
            return indexInContentEdge * contentMatrix.getColumns();
        }
        if (direction == RIGHT) {
            return indexInContentEdge * contentMatrix.getColumns() + contentMatrix.getColumns() - 1;
        }
        throw new IllegalArgumentException(String.format("Unknown direction %d", direction));
    }

    private TileContent getResolvedContentAt(int row, int column) {
        TileContent tileContent = contentMatrix.get(row, column);
        if (tileContent == TileContent.SHARED) {
            return resolveSharedTileContent(row, column);
        }
        return tileContent;
    }

    private TileContent resolveSharedTileContent(int row, int column) {
        List<TileContent> neighbors = new ArrayList<>();

        if (column > 0) neighbors.add(contentMatrix.get(row, column - 1));
        if (column < contentMatrix.getColumns() - 1) neighbors.add(contentMatrix.get(row, column + 1));
        if (row > 0) neighbors.add(contentMatrix.get(row - 1, column));
        if (row < contentMatrix.getRows() - 1) neighbors.add(contentMatrix.get(row + 1, column));

        return TileContent.getById(TileContent.merge(neighbors.toArray(TileContent[]::new)));
    }

    public static boolean compareTileEdgesWithIndex(TileContent[] lhs, TileContent[] rhs, AdjacentTileEdgesWithIndexVisitor consumer) {
        if (lhs.length == rhs.length) {
            for (int i = 0; i < lhs.length; i++) {
                if (!consumer.accept(lhs[i], i, rhs[i], i)) {
                    return false;
                }
            }
        } else {
            TileContent[] smallEdge;
            TileContent[] largeEdge;
            if (lhs.length > rhs.length) {
                smallEdge = rhs;
                largeEdge = lhs;
            } else {
                smallEdge = lhs;
                largeEdge = rhs;
            }
            int sizeDifference = largeEdge.length - smallEdge.length - 1;

            // match center
            int largeHalf = largeEdge.length / 2;
            int smallHalf = smallEdge.length / 2;
            if (!consumer.accept(largeEdge[largeHalf], largeHalf, smallEdge[smallHalf], smallHalf)) {
                return false;
            }

            for (int smallIndex = 0; smallIndex < smallEdge.length / 2; smallIndex++) {
                for (int largeIndex = 0; largeIndex < sizeDifference; largeIndex++) {
                    int upperSmallIndex = smallHalf + smallIndex + 1;
                    int upperLargeIndex = largeHalf + (smallIndex * 2) + largeIndex + 1;
                    boolean upperHalf = consumer.accept(smallEdge[upperSmallIndex], upperSmallIndex, largeEdge[upperLargeIndex], upperLargeIndex);

                    int lowerSmallIndex = smallHalf - smallIndex + 1;
                    int lowerLargeIndex = largeHalf - (smallIndex * 2) + largeIndex + 1;
                    boolean lowerHalf = consumer.accept(smallEdge[lowerSmallIndex], lowerSmallIndex, largeEdge[lowerLargeIndex], lowerLargeIndex);
                    if (!(upperHalf || lowerHalf)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean compareTileEdges(TileContent[] lhs, TileContent[] rhs, AdjacentTileEdgesVisitor consumer) {
        return compareTileEdgesWithIndex(lhs, rhs, (lhsEdge, lhsIndex, rhsEdge, rhsIndex) -> consumer.accept(lhsEdge, rhsEdge));
    }

    public static Builder<MatrixTileLayout> builder() {
        return new MatrixTileLayoutBuilder();
    }

    @FunctionalInterface
    public interface AdjacentTileEdgesVisitor {
        boolean accept(TileContent lhs, TileContent rhs);
    }

    @FunctionalInterface
    public interface AdjacentTileEdgesWithIndexVisitor {
        boolean accept(TileContent lhs, int lhsIndex, TileContent rhs, int rhsIndex);
    }

    static final class MatrixTileLayoutBuilder implements Builder<MatrixTileLayout> {

        private final TileContent[] values;

        public MatrixTileLayoutBuilder() {
            this.values = new TileContent[DEFAULT_DIMENSIONS * DEFAULT_DIMENSIONS];
        }

        @Override
        public MatrixTileLayoutBuilder setBackground(TileContent content) {
            Arrays.fill(values, content);
            return this;
        }

        @Override
        public MatrixTileLayoutBuilder setLeftEdge(TileContent content) {
            values[0] = content;
            values[3] = content;
            values[6] = content;
            return this;
        }

        @Override
        public MatrixTileLayoutBuilder setRightEdge(TileContent content) {
            values[2] = content;
            values[5] = content;
            values[8] = content;
            return this;
        }

        @Override
        public MatrixTileLayoutBuilder setTopEdge(TileContent content) {
            values[0] = content;
            values[1] = content;
            values[2] = content;
            return this;
        }

        @Override
        public MatrixTileLayoutBuilder setBottomEdge(TileContent content) {
            values[6] = content;
            values[7] = content;
            values[8] = content;
            return this;
        }

        @Override
        public MatrixTileLayout setAll(TileContent content) {
            return new MatrixTileLayout(new Matrix<>(1, 1, new TileContent[]{ content }));
        }

        @Override
        public MatrixTileLayout setValues(int rows, int columns, TileContent[] contents) {
            return new MatrixTileLayout(new Matrix<>(rows, columns, contents));
        }

        @Override
        public MatrixTileLayout build() {
            return new MatrixTileLayout(new Matrix<>(DEFAULT_DIMENSIONS, DEFAULT_DIMENSIONS, values));
        }
    }
}
