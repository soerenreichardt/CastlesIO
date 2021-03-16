package io.castles.core.board.statistics;

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
}