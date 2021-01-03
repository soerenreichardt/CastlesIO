package io.castles.core.tile;

public final class TileUtil {

    static int oppositeDirection(int direction) {
        if (direction == TileLayout.LEFT) return TileLayout.RIGHT;
        if (direction == TileLayout.RIGHT) return TileLayout.LEFT;
        if (direction == TileLayout.TOP) return TileLayout.BOTTOM;
        if (direction == TileLayout.BOTTOM) return TileLayout.TOP;

        throw new IllegalArgumentException(String.format("No direction match input %d", direction));
    }
}
