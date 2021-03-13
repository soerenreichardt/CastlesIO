package io.castles.core.tile;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class MatrixTileLayout extends AbstractTileLayout<MatrixTileLayout> {

    private final Matrix<TileContent> contentMatrix;

    public MatrixTileLayout(Matrix<TileContent> contentMatrix) {
        this.contentMatrix = contentMatrix;
    }

    @Override
    protected boolean matchesTileWithAppliedRotation(TileContent[] otherTileEdge, int rotatedDirection) {
        TileContent[] tileEdge = getTileContentEdgeWithAppliedRotation(rotatedDirection);
        if (tileEdge.length == otherTileEdge.length) {
            for (int i = 0; i < tileEdge.length; i++) {
                if (tileEdge[i] != otherTileEdge[i]) {
                    return false;
                }
            }
        } else {
            var baseContent = tileEdge[0];
            for (int i = 1; i < tileEdge.length; i++) {
                if (tileEdge[i] != baseContent) {
                    return false;
                }
            }
            var otherBaseContent = otherTileEdge[0];
            for (int i = 1; i < otherTileEdge.length; i++) {
                if (otherTileEdge[i] != otherBaseContent) {
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
                edge[i] = contentMatrix.get(i, 0);
            }
        }
        if (rotatedDirection == RIGHT) {
            for (int i = 0; i < edgeLength; i++) {
                edge[i] = contentMatrix.get(i, contentMatrix.getColumns() - 1);
            }
        }
        if (rotatedDirection == TOP) {
            System.arraycopy(contentMatrix.getValues(), 0, edge, 0, edgeLength);
        }
        if (rotatedDirection == BOTTOM) {
            System.arraycopy(contentMatrix.getValues(), contentMatrix.getValues().length - contentMatrix.getColumns(), edge, 0, edgeLength);
        }

        return edge;
    }
}
