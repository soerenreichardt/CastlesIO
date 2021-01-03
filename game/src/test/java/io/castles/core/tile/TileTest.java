package io.castles.core.tile;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void shouldCreateRandomTile() {
        var tile = Tile.drawRandom();
        assertNotNull(tile);
        for (TileContent tileContent : tile.getTileEdges()) {
            assertNotNull(tileContent);
        }
    }

    @Test
    void shouldCreateSpecificTile() {
        Tile tile = Tile.drawSpecific(TileContent.GRAS, TileContent.CASTLE, TileContent.STREET, null);
        TileContent[] tileContents = tile.getTileEdges();
        assertNotNull(tileContents);
        assertEquals(TileContent.GRAS, tileContents[TileLayout.LEFT]);
        assertEquals(TileContent.CASTLE, tileContents[TileLayout.RIGHT]);
        assertEquals(TileContent.STREET, tileContents[TileLayout.TOP]);
        assertNull(tileContents[TileLayout.BOTTOM]);
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
        Tile tile = Tile.drawStatic(TileContent.GRAS);
        for (TileContent tileContent : tile.getTileEdges()) {
            assertEquals(TileContent.GRAS, tileContent);
        }

        tile.insertToBoard(0, 1);
        for (TileContent tileContent : tile.getTileEdges()) {
            assertEquals(TileContent.GRAS, tileContent);
        }
    }

    @Test
    void shouldRotateTile() {
        var tile = Tile.drawSpecific(TileContent.GRAS, TileContent.GRAS, TileContent.STREET, TileContent.CASTLE);
        Tile expectedRotation = Tile.drawSpecific(TileContent.CASTLE, TileContent.STREET, TileContent.GRAS, TileContent.GRAS);
        tile.rotate();
        for (int i = 0; i < tile.getTileEdges().length; i++) {
            assertEquals(expectedRotation.getTileEdges()[i], tile.getTileEdges()[i]);
        }
    }

    @Test
    void shouldEqualSimilarTiles() {
        var id = UUID.randomUUID();
        Tile templateTile = Tile.drawStatic(TileContent.GRAS);
        Tile tile = new Tile(id, templateTile.getTileLayout());

        assertEquals(tile, new Tile(id, templateTile.getTileLayout()));
        assertNotEquals(tile, new Tile(UUID.randomUUID(), templateTile.getTileLayout()));
    }

}