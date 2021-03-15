package io.castles.core.util;

import io.castles.core.tile.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonTileLoaderTest {

    @Test
    void shouldLoadTileFromJson() throws IOException {
        var tileLoader = new JsonTileLoader();
        var expectedTileLayout = MatrixTileLayout.builder()
                .setValues(3, 3, new TileContent[]{
                                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                                TileContent.GRAS, TileContent.GRAS, TileContent.CASTLE,
                                TileContent.GRAS, TileContent.CASTLE, TileContent.CASTLE
                        }
                );
        var expectedTile = new Tile(1, expectedTileLayout);
        List<Tile> tiles = tileLoader.getTilesFromResource("tiles.json");
        assertEquals(1, tiles.size());
        assertEquals(expectedTile, tiles.get(0));
    }
}