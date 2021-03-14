package io.castles.core.tile;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
abstract class AbstractTile {
    protected final TileLayout tileLayout;

    AbstractTile(TileLayout tileLayout) {
        this.tileLayout = tileLayout;
    }

    abstract void setNeighbor(int position, Tile tile);

    abstract int getX();

    abstract int getY();

    abstract void rotate();

    TileLayout getTileLayout() {
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
