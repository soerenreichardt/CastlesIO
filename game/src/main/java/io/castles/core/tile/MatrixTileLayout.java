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
        if (tileEdge.length == otherTileEdge.length) {
            for (int i = 0; i < tileEdge.length; i++) {
                if (!tileEdge[i].matches(otherTileEdge[i])) {
                    return false;
                }
            }
        } else {
            var baseContent = tileEdge[0];
            for (int i = 1; i < tileEdge.length; i++) {
                if (!tileEdge[i].matches(baseContent)) {
                    return false;
                }
            }
            var otherBaseContent = otherTileEdge[0];
            for (int i = 1; i < otherTileEdge.length; i++) {
                if (!otherTileEdge[i].matches(otherBaseContent)) {
                    return false;
                }
            }
            return baseContent == otherBaseContent;
        }
        return true;
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

    public static Builder<MatrixTileLayout> builder() {
        return new MatrixTileLayoutBuilder();
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
