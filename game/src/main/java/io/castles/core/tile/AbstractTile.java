package io.castles.core.tile;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
abstract class AbstractTile {
    protected final TileContent[] tileContents;

    AbstractTile(TileContent[] tileContents) {
        this.tileContents = tileContents;
    }

    abstract void setNeighbor(int position, Tile tile);

    abstract int getX();

    abstract int getY();

    abstract void rotate();

    abstract TileContent[] tileBorders();

    abstract Tile[] neighbors();

}
