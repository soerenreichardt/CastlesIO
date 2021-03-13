package io.castles.core.tile;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
abstract class AbstractTile<T extends TileLayout<T>> {
    protected final T tileLayout;

    AbstractTile(T tileLayout) {
        this.tileLayout = tileLayout;
    }

    abstract void setNeighbor(int position, Tile tile);

    abstract int getX();

    abstract int getY();

    abstract void rotate();

    T getTileLayout() {
        return this.tileLayout;
    }

    TileContent[] getTileEdges() {
        var singleTypeTileEdges = new TileContent[TileLayout.NUM_EDGES];
        for (int i = 0; i < TileLayout.NUM_EDGES; i++) {
            TileContent[] tileContentEdge = tileLayout.getTileContentEdge(i);
            singleTypeTileEdges[i] = tileContentEdge[tileContentEdge.length / 2];
        }
        return singleTypeTileEdges;
    }

    abstract Tile[] neighbors();

}
