package io.castles.core.tile;

public enum TileContent {
    GRAS(0),
    CASTLE(1),
    STREET(2);

    private final int id;

    TileContent(int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }

    static TileContent getById(int id) {
        for (TileContent tileContent : values()) {
            if (tileContent.getId() == id) {
                return tileContent;
            }
        }

        throw new IllegalArgumentException(String.format("Id %d is not within range (0,%d)", id, values().length));
    }
}
