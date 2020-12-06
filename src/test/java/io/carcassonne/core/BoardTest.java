package io.carcassonne.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void createsBoardWithInitialTile() {
        var board = new Board();
        assertNotNull(board);

        var tile = board.getTile(0, 0);
        assertEquals(0, tile.getX());
        assertEquals(0, tile.getY());
        for (Tile neighbor : tile.getNeighbors()) {
            assertNull(neighbor);
        }
    }

    @Test
    void shouldInsertNewTiles() {
        var board = new Board();
        var tile = Tile.drawRandom();

        assertTrue(board.insertTileToBoard(0, 1, tile));
        assertTrue(board.insertTileToBoard(0, 2, tile));
        assertTrue(board.insertTileToBoard(1, 0, tile));
    }

    @Test
    void shouldThrowOnInsertIfNoNeighborsWereFound() {
        var board = new Board();
        var tile = Tile.drawRandom();

        assertThatThrownBy(() -> board.insertTileToBoard(1, 1, tile))
                .hasMessageContaining("no neighbors were found")
                .isInstanceOf(IllegalArgumentException.class);
    }
}