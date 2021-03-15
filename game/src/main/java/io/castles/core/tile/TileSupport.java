package io.castles.core.tile;

public class TileSupport {

    public static int oppositeDirection(int direction) {
        return (direction + 2) % 4;
    }
}
