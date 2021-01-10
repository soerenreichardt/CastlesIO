package io.castles.core;

import io.castles.core.board.Board;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void createsBoardWithInitialTile() {
        var board = Board.withStaticTile(TileContent.GRAS);
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
        var board = Board.withStaticTile(TileContent.GRAS);

        for (TileContent tileContent : board.getTile(0, 0).getTileEdges()) {
            assertEquals(TileContent.GRAS, tileContent);
        }
    }

    @Test
    void shouldInsertNewTiles() {
        var board = Board.withStaticTile(TileContent.GRAS);
        var tile = Tile.drawStatic(TileContent.GRAS);

        assertDoesNotThrow(() -> board.insertTileToBoard(tile, 0, 1));
        assertDoesNotThrow(() -> board.insertTileToBoard(tile, 0, 2));
        assertDoesNotThrow(() -> board.insertTileToBoard(tile, 1, 0));
    }

    @Test
    void shouldThrowWhenInsertingToOccupiedPosition() {
        var board = Board.withStaticTile(TileContent.GRAS);
        var tile = Tile.drawStatic(TileContent.GRAS);
        assertThatThrownBy(() -> board.insertTileToBoard(tile, 0, 0))
                .hasMessageContaining("already occupied")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnInsertIfNoNeighborsWereFound() {
        var board = Board.withStaticTile(TileContent.GRAS);
        var tile = Tile.drawStatic(TileContent.GRAS);

        assertThatThrownBy(() -> board.insertTileToBoard(tile, 1, 1))
                .hasMessageContaining("no neighbors were found")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenInsertingWithIncompatibleBorders() {
        var board = Board.withStaticTile(TileContent.GRAS);
        var tile = Tile.drawStatic(TileContent.CASTLE);

        assertThatThrownBy(() -> board.insertTileToBoard(tile, 0, 1))
                .hasMessageContaining("incompatible borders")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCreateNewTiles() {
        Board board = Board.withStaticTile(TileContent.GRAS);
        Tile newTile = board.getNewTile();
        assertNotNull(newTile);
    }
}