package io.castles.core.board;

import io.castles.core.graph.Graph;
import io.castles.core.tile.Matrix;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AdjacentClosedCastlesToGrasComponentComputerTest {

    Board board;
    Matrix<TileContent> castleMatrix;

    @BeforeEach
    void setup() {
        castleMatrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.GRAS, TileContent.GRAS, TileContent.SHARED,
                TileContent.GRAS, TileContent.GRAS, TileContent.CASTLE,
                TileContent.GRAS, TileContent.GRAS, TileContent.SHARED
        });
        this.board = Board.withSpecificTile(new MatrixTileLayout(castleMatrix));
        var tile = Tile.fromMatrix(castleMatrix);
        tile.rotate();
        tile.rotate();
        this.board.insertTileToBoard(tile, 1, 0);
    }

    @Test
    void shouldFindSingleAdjacentClosedCastle() {
        this.board.insertTileToBoard(Tile.drawStatic(TileContent.GRAS), 0, -1);
        this.board.insertTileToBoard(Tile.drawStatic(TileContent.GRAS), 1, -1);
        var graph = board.getBoardGraph().filterGraphsForContent(TileContent.GRAS);

        var closedCastles = Set.of(
                Set.of(
                        new Graph.Node(0, 0, 1, 2),
                        new Graph.Node(1, 0, 1, 0)
                )
        );
        var closedCastlesForGrasComponents = new AdjacentClosedCastlesToGrasComponentComputer(graph, closedCastles, board::getTile).compute();
        assertThat(closedCastlesForGrasComponents.size()).isEqualTo(1);
        assertThat(closedCastlesForGrasComponents.get(0).getClosedCastles()).isEqualTo(1);
    }

    @Test
    void shouldFindMultipleAdjacentClosedCastles() {
        this.board.insertTileToBoard(Tile.drawStatic(TileContent.GRAS), 0, -1);
        this.board.insertTileToBoard(Tile.drawStatic(TileContent.GRAS), 1, -1);

        var graph = board.getBoardGraph().filterGraphsForContent(TileContent.GRAS);

        this.board.insertTileToBoard(Tile.fromMatrix(castleMatrix), 0, -2);
        var tile = Tile.fromMatrix(castleMatrix);
        tile.rotate();
        tile.rotate();
        this.board.insertTileToBoard(tile, 1, -2);

        var closedCastles = Set.of(
                Set.of(
                        new Graph.Node(0, 0, 1, 2),
                        new Graph.Node(1, 0, 1, 0)
                ),
                Set.of(
                        new Graph.Node(0, -2, 1, 2),
                        new Graph.Node(1, -2, 1, 0)
                )
        );

        var closedCastlesForGrasComponents = new AdjacentClosedCastlesToGrasComponentComputer(graph, closedCastles, board::getTile).compute();
        assertThat(closedCastlesForGrasComponents.size()).isEqualTo(1);
        assertThat(closedCastlesForGrasComponents.get(0).getClosedCastles()).isEqualTo(2);
    }

    @Test
    void shouldFindSameCastleForMultipleRegions() {
        var graph = board.getBoardGraph().filterGraphsForContent(TileContent.GRAS);
        var closedCastles = Set.of(
                Set.of(
                        new Graph.Node(0, 0, 1, 2),
                        new Graph.Node(1, 0, 1, 0)
                )
        );
        var closedCastlesForGrasComponents = new AdjacentClosedCastlesToGrasComponentComputer(graph, closedCastles, board::getTile).compute();
        assertThat(closedCastlesForGrasComponents.size()).isEqualTo(2);
    }

}