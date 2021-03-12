package io.castles.core.tile;

public class MatrixTileLayout implements TileLayout {

    private final Matrix<TileContent> contentMatrix;

    public MatrixTileLayout(Matrix<TileContent> contentMatrix) {
        this.contentMatrix = contentMatrix;
    }

    @Override
    public boolean matches(TileLayout other, int direction) {
        return false;
    }

    @Override
    public void rotate() {

    }

    TileContent[] getTileContentEdge(int direction) {
        int edgeLength = direction == LEFT || direction == RIGHT
                ? contentMatrix.getRows()
                : contentMatrix.getColumns();
        TileContent[] edge = new TileContent[edgeLength];

        if (direction == LEFT) {
            for (int i = 0; i < edgeLength; i++) {
                edge[i] = contentMatrix.get(i, 0);
            }
        }
        if (direction == RIGHT) {
            for (int i = 0; i < edgeLength; i++) {
                edge[i] = contentMatrix.get(i, contentMatrix.getColumns() - 1);
            }
        }
        if (direction == TOP) {
            System.arraycopy(contentMatrix.getValues(), 0, edge, 0, edgeLength);
        }
        if (direction == BOTTOM) {
            System.arraycopy(contentMatrix.getValues(), contentMatrix.getValues().length - contentMatrix.getColumns(), edge, 0, edgeLength);
        }

        return edge;
    }
}
