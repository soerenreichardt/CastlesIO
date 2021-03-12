package io.castles.core.tile;

public class MatrixTileLayout extends AbstractTileLayout {

    private final Matrix<TileContent> contentMatrix;

    public MatrixTileLayout(Matrix<TileContent> contentMatrix) {
        this.contentMatrix = contentMatrix;
    }

    @Override
    protected boolean matchesTileWithAppliedRotation(TileLayout other, int rotatedDirection) {
        return false;
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
