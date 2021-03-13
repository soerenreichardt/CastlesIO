package io.castles.core.tile;

public interface TileLayout<SELF extends TileLayout<SELF>> {

    int NUM_EDGES = 4;

    int LEFT = 0;
    int TOP = 1;
    int RIGHT = 2;
    int BOTTOM = 3;
    int MIDDLE = 4;

    boolean matches(SELF other, int direction);

    void rotate();

    default void rotate(int times) {
        for (int i = 0; i < times; i++) {
            rotate();
        }
    }
}
