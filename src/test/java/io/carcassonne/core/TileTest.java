package io.carcassonne.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TileTest {

    @Test
    void shouldCreateTile() {
        var tile = Tile.drawRandom();
        assertNotNull(tile);
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

}