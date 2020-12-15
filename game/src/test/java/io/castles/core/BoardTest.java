package io.castles.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void createsBoardWithInitialTile() {
        var board = Board.withRandomTile();
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
        var board = Board.withStaticTile(Tile.TileBorder.GRAS);
        var tile = Tile.drawStatic(Tile.TileBorder.GRAS);

        assertDoesNotThrow(() -> board.insertTileToBoard(0, 1, tile));
        assertDoesNotThrow(() -> board.insertTileToBoard(0, 2, tile));
        assertDoesNotThrow(() -> board.insertTileToBoard(1, 0, tile));
    }

    @Test
    void shouldThrowWhenInsertingToOccupiedPosition() {
        var board = Board.withRandomTile();
        var tile = Tile.drawStatic(Tile.TileBorder.GRAS);
        assertThatThrownBy(() -> board.insertTileToBoard(0, 0, tile))
                .hasMessageContaining("already occupied")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnInsertIfNoNeighborsWereFound() {
        var board = Board.withRandomTile();
        var tile = Tile.drawRandom();

        assertThatThrownBy(() -> board.insertTileToBoard(1, 1, tile))
                .hasMessageContaining("no neighbors were found")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenInsertingWithIncompatibleBorders() {
        var board = Board.withStaticTile(Tile.TileBorder.GRAS);
        var tile = Tile.drawStatic(Tile.TileBorder.CASTLE);

        assertThatThrownBy(() -> board.insertTileToBoard(0, 1, tile))
                .hasMessageContaining("incompatible borders")
                .isInstanceOf(IllegalArgumentException.class);
    }
}