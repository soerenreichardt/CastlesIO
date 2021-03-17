package io.castles.core.board.statistics;

import io.castles.core.board.Board;
import io.castles.core.tile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardStatisticsTest {

    TileLayout startLayout;

    @BeforeEach
    void setup() {
        Matrix<TileContent> startMatrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                TileContent.STREET, TileContent.STREET, TileContent.STREET,
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS
        });
        startLayout = new MatrixTileLayout(startMatrix);
    }

    @Test
    void shouldComputeStreetLengths() {
        Matrix<TileContent> streetEndMatrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                TileContent.GRAS, TileContent.STREET, TileContent.STREET,
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS
        });
        var leftTile = new Tile(new MatrixTileLayout(streetEndMatrix));
        MatrixTileLayout streetEndLayout = new MatrixTileLayout(streetEndMatrix);
        streetEndLayout.rotate(2);
        var rightTile = new Tile(streetEndLayout);

        Board board = Board.withSpecificTile(startLayout);
        board.insertTileToBoard(leftTile, -1, 0);
        board.insertTileToBoard(rightTile, 1, 0);

        int streetLength = board.getBoardStatistics().getStreetLength(board.getTile(0, 0), 1, 1);
        assertThat(streetLength).isEqualTo(3);
    }
}