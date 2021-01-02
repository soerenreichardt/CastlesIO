package io.castles.core.tile;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
abstract class AbstractTile {
    protected final TileBorder[] tileBorders;

    AbstractTile(TileBorder[] tileBorders) {
        this.tileBorders = tileBorders;
    }

    abstract void setNeighbor(int position, Tile tile);

    abstract int getX();

    abstract int getY();

    abstract void rotate();

    abstract TileBorder[] tileBorders();

    abstract Tile[] neighbors();

}
