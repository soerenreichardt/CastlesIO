package io.castles.core.board.statistics;

import io.castles.core.board.Board;
import io.castles.core.board.BoardGraph;
import io.castles.core.graph.Graph;
import io.castles.core.tile.*;
import io.castles.exceptions.GrasRegionOccupiedException;
import io.castles.game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardGraphTest {

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

        int streetLength = board.getBoardGraph().getStreetLength(board.getTile(0, 0), 1, 1);
        assertThat(streetLength).isEqualTo(3);
    }

    @Test
    void shouldDetectUnclosedStreet() {
        Matrix<TileContent> streetEndMatrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                TileContent.GRAS, TileContent.STREET, TileContent.STREET,
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS
        });
        var leftTile = new Tile(new MatrixTileLayout(streetEndMatrix));

        Board board = Board.withSpecificTile(startLayout);
        board.insertTileToBoard(leftTile, -1, 0);

        int streetLength = board.getBoardGraph().getStreetLength(board.getTile(0, 0), 1, 1);
        assertThat(streetLength).isEqualTo(BoardGraph.UNCLOSED_STREET);
    }

    @Nested
    class Figures {

        Board board;

        @BeforeEach
        void setup() {
            Matrix<TileContent> streetEndMatrix = new Matrix<>(3, 3, new TileContent[]{
                    TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                    TileContent.GRAS, TileContent.STREET, TileContent.STREET,
                    TileContent.GRAS, TileContent.GRAS, TileContent.GRAS
            });
            var leftTile = new Tile(new MatrixTileLayout(streetEndMatrix));

            board = Board.withSpecificTile(startLayout);
            board.insertTileToBoard(leftTile, -1, 0);
        }

        @AfterEach
        void tearDown() {
            board.restart();
        }

        @Test
        void shouldBeAbleToPlaceValidFigure() throws GrasRegionOccupiedException {
            var p1 = new Player("P1");
            var p2 = new Player("P2");
            Matrix<TileContent> streetEndMatrix = new Matrix<>(3, 3, new TileContent[]{
                    TileContent.GRAS, TileContent.CASTLE, TileContent.GRAS,
                    TileContent.STREET, TileContent.CASTLE, TileContent.STREET,
                    TileContent.GRAS, TileContent.CASTLE, TileContent.GRAS
            });
            var rightTile = new Tile(new MatrixTileLayout(streetEndMatrix));

            board.insertTileToBoard(rightTile, 1, 0);

            board.getBoardGraph().validateUniqueFigurePositionInWcc(
                    new Figure(new Graph.Node(1, 0, 0, 2), p2),
                    List.of(
                            new Figure(new Graph.Node(0, 0, 0, 2), p1)
                    )
            );
        }

        @ParameterizedTest
        @CsvSource(value = {
                "-1, 0, 0, 0",
                "0, 0, 2, 0"
        })
        void shouldDetectOtherFiguresInWcc(int tileX, int tileY, int tileRow, int tileColumn) {
            var p1 = new Player("P1");
            var p2 = new Player("P2");
            assertThatThrownBy(() -> board.getBoardGraph().validateUniqueFigurePositionInWcc(
                    new Figure(new Graph.Node(tileX, tileY, tileRow, tileColumn), p2),
                    List.of(
                            new Figure(new Graph.Node(0, 0, 0, 2), p1)
                    )
            )).isInstanceOf(GrasRegionOccupiedException.class);
        }

    }
}