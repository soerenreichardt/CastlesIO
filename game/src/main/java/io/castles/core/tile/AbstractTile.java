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
        return this.tileLayout.getTileEdges();
    }

    abstract Tile[] neighbors();

}
