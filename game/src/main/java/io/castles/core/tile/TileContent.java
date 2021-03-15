package io.castles.core.tile;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum TileContent {
    GRAS(0),
    CASTLE(1),
    STREET(2),
    CHURCH(3),
    SHARED(4),
    GRAS_AND_CASTLE(merge(GRAS, CASTLE), false),
    GRAS_AND_STREET(merge(GRAS, STREET), false);

    private final int id;
    private final boolean visible;

    TileContent(int id) {
        this(id, true);
    }

    TileContent(int id, boolean visible) {
        this.id = id;
        this.visible = visible;
    }

    int getId() {
        return id;
    }

    public boolean matches(TileContent other) {
        if (getId() < 0 && other.getId() < 0) return true;
        if (getId() < 0) {
            return (getId() & 1 << other.getId()) != 0;
        }
        if (other.getId() < 0) {
            return (1 << getId() & other.getId()) != 0;
        }
        return this == other;
    }

    static TileContent getById(int id) {
        for (TileContent tileContent : values()) {
            if (tileContent.getId() == id) {
                return tileContent;
            }
        }

        throw new IllegalArgumentException(String.format("Id %d is not one of %s", id, Arrays.stream(values()).map(TileContent::getId).collect(Collectors.toList())));
    }

    public static int merge(TileContent... contents) {
        // contents are all the same, prevent switching to negative id space
        if (Arrays.stream(contents).allMatch(content -> content == contents[0])) {
            return contents[0].getId();
        }
        // use negative id space to avoid clashes with defined enums
        // e.g. GRAS | CASTLE = 0 << 2 | 1 << 2 yields 3 which is a single enum type
        // and not a composite one
        int id = Integer.MIN_VALUE;
        for (TileContent content : contents) {
            id |= 1 << content.getId();
        }
        return id;
    }
}
