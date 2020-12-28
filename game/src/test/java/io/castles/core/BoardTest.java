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
    void createBoardWithStaticTile() {
        var board = Board.withStaticTile(Tile.TileBorder.GRAS);

        for (Tile.TileBorder tileBorder : board.getTile(0, 0).getTileBorders()) {
            assertEquals(Tile.TileBorder.GRAS, tileBorder);
        }
    }

    @Test
    void shouldInsertNewTiles() {
        var board = Board.withStaticTile(Tile.TileBorder.GRAS);
        var tile = Tile.drawStatic(Tile.TileBorder.GRAS);

        assertDoesNotThrow(() -> board.insertTileToBoard(tile, 0, 1));
        assertDoesNotThrow(() -> board.insertTileToBoard(tile, 0, 2));
        assertDoesNotThrow(() -> board.insertTileToBoard(tile, 1, 0));
    }

    @Test
    void shouldThrowWhenInsertingToOccupiedPosition() {
        var board = Board.withRandomTile();
        var tile = Tile.drawStatic(Tile.TileBorder.GRAS);
        assertThatThrownBy(() -> board.insertTileToBoard(tile, 0, 0))
                .hasMessageContaining("already occupied")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnInsertIfNoNeighborsWereFound() {
        var board = Board.withRandomTile();
        var tile = Tile.drawRandom();

        assertThatThrownBy(() -> board.insertTileToBoard(tile, 1, 1))
                .hasMessageContaining("no neighbors were found")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenInsertingWithIncompatibleBorders() {
        var board = Board.withStaticTile(Tile.TileBorder.GRAS);
        var tile = Tile.drawStatic(Tile.TileBorder.CASTLE);

        assertThatThrownBy(() -> board.insertTileToBoard(tile, 0, 1))
                .hasMessageContaining("incompatible borders")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCreateNewTiles() {
        Board board = Board.withRandomTile();
        Tile newTile = board.getNewTile();
        assertNotNull(newTile);
    }
}