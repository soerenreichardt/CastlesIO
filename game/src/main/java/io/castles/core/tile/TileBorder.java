package io.castles.core.tile;

public enum TileBorder {
    GRAS(0),
    CASTLE(1),
    STREET(2);

    private final int id;

    TileBorder(int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }

    static TileBorder getById(int id) {
        for (TileBorder tileBorder : values()) {
            if (tileBorder.getId() == id) {
                return tileBorder;
            }
        }

        throw new IllegalArgumentException(String.format("Id %d is not within range (0,%d)", id, values().length));
    }
}
