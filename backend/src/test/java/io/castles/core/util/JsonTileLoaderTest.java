package io.castles.core.util;

import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.core.tile.TileLayout;
import io.castles.core.tile.TileLayoutImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonTileLoaderTest {

    @Test
    void shouldLoadTileFromJson() throws IOException {
        var tileLoader = new JsonTileLoader();
        var expectedTileLayout = TileLayoutImpl.builder()
                .withContent(TileContent.GRAS).connectedOnEdges(TileLayout.LEFT, TileLayout.TOP)
                .withContent(TileContent.CASTLE).connectedOnEdges(TileLayout.RIGHT, TileLayout.BOTTOM)
                .build();
        var expectedTile = new Tile(1, expectedTileLayout);
        List<Tile> tiles = tileLoader.getTilesFromResource("tiles.json");
        assertEquals(1, tiles.size());
        assertEquals(expectedTile, tiles.get(0));
    }
}