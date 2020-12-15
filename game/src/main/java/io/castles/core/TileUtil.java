package io.castles.core;

public final class TileUtil {

    static int oppositeDirection(int direction) {
        if (direction == Tile.LEFT) return Tile.RIGHT;
        if (direction == Tile.RIGHT) return Tile.LEFT;
        if (direction == Tile.TOP) return Tile.BOTTOM;
        if (direction == Tile.BOTTOM) return Tile.TOP;

        throw new IllegalArgumentException(String.format("No direction match input %d", direction));
    }
}
