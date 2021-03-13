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
        if (times < 0) {
            times = NUM_EDGES - (Math.abs(times) % NUM_EDGES);
        }
        for (int i = 0; i < (times % NUM_EDGES); i++) {
            rotate();
        }
    }
}
