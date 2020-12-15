package io.castles.core;

import io.castles.core.Tile.TileBorder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void shouldCreateRandomTile() {
        var tile = Tile.drawRandom();
        assertNotNull(tile);
        for (TileBorder tileBorder : tile.getTileBorders()) {
            assertNotNull(tileBorder);
        }
    }

    @Test
    void shouldCreateSpecificTile() {
        Tile tile = Tile.drawSpecific(TileBorder.GRAS, TileBorder.CASTLE, TileBorder.STREET, null);
        TileBorder[] tileBorders = tile.getTileBorders();
        assertNotNull(tileBorders);
        assertEquals(TileBorder.GRAS, tileBorders[Tile.LEFT]);
        assertEquals(TileBorder.CASTLE, tileBorders[Tile.RIGHT]);
        assertEquals(TileBorder.STREET, tileBorders[Tile.TOP]);
        assertNull(tileBorders[Tile.BOTTOM]);
    }

    @Test
    void shouldInsertATileToTheBoard() {
        var tile = Tile.drawRandom();
        assertThatThrownBy(tile::getX)
                .hasMessageContaining("getX is not supported on an uninserted tile")
                .isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(tile::getY)
                .hasMessageContaining("getY is not supported on an uninserted tile")
                .isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> tile.setNeighbor(0, tile))
                .hasMessageContaining("setNeighbor is not supported on an uninserted tile")
                .isInstanceOf(UnsupportedOperationException.class);

        tile.insertToBoard(0, 1);

        assertEquals(0, tile.getX());
        assertEquals(1, tile.getY());
    }

    @Test
    void bordersShouldStayTheSameWhenInsertingToBoard() {
        Tile tile = Tile.drawStatic(TileBorder.GRAS);
        for (TileBorder tileBorder : tile.getTileBorders()) {
            assertEquals(TileBorder.GRAS, tileBorder);
        }

        tile.insertToBoard(0, 1);
        for (TileBorder tileBorder : tile.getTileBorders()) {
            assertEquals(TileBorder.GRAS, tileBorder);
        }
    }

}