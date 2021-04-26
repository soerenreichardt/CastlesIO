package io.castles.core.board.statistics;

import io.castles.core.board.Board;
import io.castles.core.board.BoardGraph;
import io.castles.core.graph.Graph;
import io.castles.core.tile.*;
import io.castles.exceptions.RegionOccupiedException;
import io.castles.game.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Set;

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

        int streetLength = board.getBoardGraph().getStreetLength(board.getTile(0, 0));
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

        int streetLength = board.getBoardGraph().getStreetLength(board.getTile(0, 0));
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
        void shouldBeAbleToPlaceValidFigure() throws RegionOccupiedException {
            var p1 = new Player("P1");
            var p2 = new Player("P2");
            Matrix<TileContent> streetEndMatrix = new Matrix<>(3, 3, new TileContent[]{
                    TileContent.GRAS, TileContent.CASTLE, TileContent.GRAS,
                    TileContent.STREET, TileContent.CASTLE, TileContent.STREET,
                    TileContent.GRAS, TileContent.CASTLE, TileContent.GRAS
            });
            var rightTile = new Tile(new MatrixTileLayout(streetEndMatrix));

            board.insertTileToBoard(rightTile, 1, 0);

            // place on gras
            board.getBoardGraph().validateFigurePlacement(
                    new Figure(new Graph.Node(1, 0, 0, 2), p2),
                    List.of(
                            new Figure(new Graph.Node(0, 0, 0, 2), p1)
                    )
            );

            // place on castle
            board.getBoardGraph().validateFigurePlacement(
                    new Figure(new Graph.Node(1, 0, 0, 1), p2),
                    List.of()
            );
        }

        @ParameterizedTest
        @CsvSource(value = {
                "-1, 0, 0, 0",
                "0, 0, 2, 0"
        })
        void shouldDetectOtherFiguresOnGrasInWcc(int tileX, int tileY, int tileRow, int tileColumn) {
            var p1 = new Player("P1");
            var p2 = new Player("P2");

            assertThatThrownBy(() -> board.getBoardGraph().validateFigurePlacement(
                    new Figure(new Graph.Node(tileX, tileY, tileRow, tileColumn), p2),
                    List.of(
                            new Figure(new Graph.Node(0, 0, 0, 2), p1)
                    )
            )).isInstanceOf(RegionOccupiedException.class);
        }

        @Test
        void shouldDetectOtherFiguresOnCastleInWcc() {
            var p1 = new Player("P1");
            var p2 = new Player("P2");

            Matrix<TileContent> streetEndMatrix = new Matrix<>(3, 3, new TileContent[]{
                    TileContent.GRAS, TileContent.CASTLE, TileContent.GRAS,
                    TileContent.STREET, TileContent.CASTLE, TileContent.STREET,
                    TileContent.GRAS, TileContent.CASTLE, TileContent.GRAS
            });
            var rightTile = new Tile(new MatrixTileLayout(streetEndMatrix));

            board.insertTileToBoard(rightTile, 1, 0);

            assertThatThrownBy(() -> board.getBoardGraph().validateFigurePlacement(
                    new Figure(new Graph.Node(1, 0, 0, 1), p2),
                    List.of(
                            new Figure(new Graph.Node(1, 0, 1, 1), p1)
                    )
            )).isInstanceOf(RegionOccupiedException.class);
        }
    }

    @Nested
    class CastleComponents {

        Board board;
        BoardGraph boardGraph;

        @BeforeEach
        void setup() {
            var tileContentMatrix = new Matrix<>(3, 3, new TileContent[]{
                    TileContent.GRAS, TileContent.GRAS, TileContent.SHARED,
                    TileContent.GRAS, TileContent.GRAS, TileContent.CASTLE,
                    TileContent.GRAS, TileContent.GRAS, TileContent.SHARED
            });
            this.board = Board.withSpecificTile(new MatrixTileLayout(tileContentMatrix));
            this.boardGraph = board.getBoardGraph();
            assertThat(boardGraph.closedCastles(board.getTile(0, 0))).isEmpty();
        }

        @Test
        void shouldDetectSingleClosedCastle() {
            var tileContentMatrix = new Matrix<>(3, 3, new TileContent[]{
                    TileContent.SHARED, TileContent.GRAS, TileContent.GRAS,
                    TileContent.CASTLE, TileContent.GRAS, TileContent.GRAS,
                    TileContent.SHARED, TileContent.GRAS, TileContent.GRAS
            });
            var tile = new Tile(new MatrixTileLayout(tileContentMatrix));
            board.insertTileToBoard(tile, 1, 0);
            var closedCastles = boardGraph.closedCastles(tile);
            assertThat(closedCastles.size()).isEqualTo(1);
            assertThat(closedCastles.iterator().next()).containsExactlyInAnyOrder(
                    new Graph.Node(0, 0, 0, 2),
                    new Graph.Node(0, 0, 1, 2),
                    new Graph.Node(0, 0, 2, 2),

                    new Graph.Node(1, 0, 0, 0),
                    new Graph.Node(1, 0, 1, 0),
                    new Graph.Node(1, 0, 2, 0)
            );
        }

        @Test
        void shouldDetectUnclosedCastle() {
            var tileContentMatrix = new Matrix<>(3, 3, new TileContent[]{
                    TileContent.SHARED, TileContent.CASTLE, TileContent.CASTLE,
                    TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE,
                    TileContent.SHARED, TileContent.CASTLE, TileContent.CASTLE
            });
            var tile = new Tile(new MatrixTileLayout(tileContentMatrix));
            board.insertTileToBoard(tile, 1, 0);
            var closedCastles = boardGraph.closedCastles(tile);
            assertThat(closedCastles).isEmpty();
        }

        @Test
        void shouldDetectMultipleClosedCastles() {
            var grasMatrix = new Matrix<>(1, 1, new TileContent[]{TileContent.GRAS});
            var grasTile = new Tile(new MatrixTileLayout(grasMatrix));
            board.insertTileToBoard(grasTile, 0, -1);

            var singleCastleMatrix = new Matrix<>(3, 3, new TileContent[] {
                    TileContent.SHARED, TileContent.CASTLE, TileContent.SHARED,
                    TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                    TileContent.GRAS, TileContent.GRAS, TileContent.GRAS
            });
            var singleCastleTile = new Tile(new MatrixTileLayout(singleCastleMatrix));

            var doubleCastleMatrix = new Matrix<>(3, 3, new TileContent[] {
                    TileContent.SHARED, TileContent.GRAS, TileContent.GRAS,
                    TileContent.CASTLE, TileContent.GRAS, TileContent.GRAS,
                    TileContent.DISCONNECTED, TileContent.CASTLE, TileContent.SHARED
            });
            var doubleCastleTile = new Tile(new MatrixTileLayout(doubleCastleMatrix));

            board.insertTileToBoard(singleCastleTile, 1, -1);
            board.insertTileToBoard(doubleCastleTile, 1, 0);

            var closedCastles = boardGraph.closedCastles(doubleCastleTile);
            assertThat(closedCastles.size()).isEqualTo(2);
            assertThat(closedCastles).contains(
                    Set.of(
                            new Graph.Node(0, 0, 0, 2),
                            new Graph.Node(0, 0, 1, 2),
                            new Graph.Node(0, 0, 2, 2),
                            new Graph.Node(1, 0, 0, 0),
                            new Graph.Node(1, 0, 1, 0)
                    ),
                    Set.of(
                            new Graph.Node(1, 0, 2, 1),
                            new Graph.Node(1, 0, 2, 2),
                            new Graph.Node(1, -1, 0, 0),
                            new Graph.Node(1, -1, 0, 1),
                            new Graph.Node(1, -1, 0, 2)
                    )
            );
        }
    }
}