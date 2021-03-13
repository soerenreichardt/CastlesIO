package io.castles.core.tile;

public interface TileLayout<SELF extends TileLayout<SELF, CONTENT>, CONTENT> {

    int NUM_EDGES = 4;

    int LEFT = 0;
    int TOP = 1;
    int RIGHT = 2;
    int BOTTOM = 3;
    int MIDDLE = 4;

    boolean matches(SELF other, int direction);

    TileContent[] getTileContentEdge(int direction);

    TileContent getCenter();

    void rotate();

    CONTENT getContent();

    default void rotate(int times) {
        if (times < 0) {
            times = NUM_EDGES - (Math.abs(times) % NUM_EDGES);
        }
        for (int i = 0; i < (times % NUM_EDGES); i++) {
            rotate();
        }
    }

    interface Builder<T> {
        Builder<T> setBackground(TileContent content);
        Builder<T> setLeftEdge(TileContent content);
        Builder<T> setRightEdge(TileContent content);
        Builder<T> setTopEdge(TileContent content);
        Builder<T> setBottomEdge(TileContent content);
        T setAll(TileContent content);
        T setValues(int rows, int columns, TileContent[] contents);

        T build();
    }
}
