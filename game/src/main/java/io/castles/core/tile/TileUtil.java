package io.castles.core.tile;

public final class TileUtil {

    static int oppositeDirection(int direction) {
        if (direction == TileLayoutImpl.LEFT) return TileLayoutImpl.RIGHT;
        if (direction == TileLayoutImpl.RIGHT) return TileLayoutImpl.LEFT;
        if (direction == TileLayoutImpl.TOP) return TileLayoutImpl.BOTTOM;
        if (direction == TileLayoutImpl.BOTTOM) return TileLayoutImpl.TOP;

        throw new IllegalArgumentException(String.format("No direction match input %d", direction));
    }
}
