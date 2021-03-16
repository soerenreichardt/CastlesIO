package io.castles.core.board.statistics;

import io.castles.core.board.Board;
import io.castles.core.graph.Graph;
import io.castles.core.tile.Matrix;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphTest {

    @Test
    void shouldCreateTileInternalGraphNodes() {
        Matrix<TileContent> matrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE
        });
        var tileLayout = new MatrixTileLayout(matrix);
        var tile = new Tile(0, tileLayout);
        tile.insertToBoard(0, 0);

        var grasGraph = new Graph(TileContent.GRAS);
        grasGraph.fromTile(tile);
        assertThat(grasGraph.nodeCount()).isEqualTo(3);
        assertThat(grasGraph.nodes()).contains(
                new Graph.Node(tile.getId(), 0, 1),
                new Graph.Node(tile.getId(), 1, 1),
                new Graph.Node(tile.getId(), 2, 1)
        );

        var castleGraph = new Graph(TileContent.CASTLE);
        castleGraph.fromTile(tile);
        assertThat(castleGraph.nodes().size()).isEqualTo(6);
        assertThat(castleGraph.nodes()).contains(
                new Graph.Node(tile.getId(), 0, 0),
                new Graph.Node(tile.getId(), 1, 0),
                new Graph.Node(tile.getId(), 2, 0),
                new Graph.Node(tile.getId(), 0, 2),
                new Graph.Node(tile.getId(), 1, 2),
                new Graph.Node(tile.getId(), 2, 2)
        );
    }

    @Test
    void shouldCreateTileInternalGraphRelationships() {
        Matrix<TileContent> matrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE
        });
        var tileLayout = new MatrixTileLayout(matrix);
        var tile = new Tile(0, tileLayout);
        tile.insertToBoard(0, 0);

        var grasGraph = new Graph(TileContent.GRAS);
        grasGraph.fromTile(tile);
        assertThat(grasGraph.relationshipCount()).isEqualTo(4);
        assertThat(grasGraph.relationships().get(new Graph.Node(tile.getId(), 0, 1)))
                .containsExactly(new Graph.Node(tile.getId(), 1, 1));
        assertThat(grasGraph.relationships().get(new Graph.Node(tile.getId(), 1, 1)))
                .containsExactly(
                        new Graph.Node(tile.getId(), 0, 1),
                        new Graph.Node(tile.getId(), 2, 1)
                );
        assertThat(grasGraph.relationships().get(new Graph.Node(tile.getId(), 2, 1)))
                .containsExactly(new Graph.Node(tile.getId(), 1, 1));
    }

    @Test
    void shouldConnectGraphsOfDifferentTiles() {
        Matrix<TileContent> matrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.GRAS, TileContent.GRAS,
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE
        });
        var tileLayout = new MatrixTileLayout(matrix);
        var board = Board.withSpecificTile(tileLayout);
        var grasGraph = new Graph(TileContent.GRAS);

        var startTile = board.getTile(0, 0);
        grasGraph.fromTile(startTile);

        var tile = board.getNewTile();
        board.insertTileToBoard(tile, 0, 1);
        grasGraph.fromTile(board.getTile(0, 1));

        assertThat(grasGraph.relationshipCount()).isEqualTo(14);
        assertThat(grasGraph.relationships().get(new Graph.Node(startTile.getId(), 0, 1)))
                .containsExactlyInAnyOrder(
                        new Graph.Node(startTile.getId(), 1, 1),
                        new Graph.Node(tile.getId(), 2, 1)
                );
    }

    @Test
    void shouldConnectGraphsOnRotatedTiles() {
        Matrix<TileContent> startMatrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.CASTLE, TileContent.GRAS,
                TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE
        });
        var startTileLayout = new MatrixTileLayout(startMatrix);
        var board = Board.withSpecificTile(startTileLayout);
        var startTile = board.getTile(0, 0);

        var graph = new Graph(TileContent.GRAS);
        graph.fromTile(startTile);

        Matrix<TileContent> nextMatrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.CASTLE, TileContent.GRAS, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE,
                TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE
        });
        var nextTileLayout = new MatrixTileLayout(nextMatrix);
        var nextTile = new Tile(nextTileLayout);
        nextTile.rotate();
        board.insertTileToBoard(nextTile, 1, 0);
        graph.fromTile(nextTile);

        assertThat(graph.relationshipCount()).isEqualTo(2);
        assertThat(graph.relationships().get(new Graph.Node(startTile.getId(), 1, 2)))
                .containsExactly(new Graph.Node(nextTile.getId(), 0, 1));
    }
}