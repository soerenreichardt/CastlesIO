package io.castles.exceptions;

import io.castles.core.tile.TileContent;

public class RegionOccupiedException extends Exception {
    public RegionOccupiedException(TileContent tileContent) {
        super(getErrorMessage(tileContent));
    }

    private static String getErrorMessage(TileContent tileContent) {
        return String.format("%s region occupied by other figure", tileContent.name());
    }
}
