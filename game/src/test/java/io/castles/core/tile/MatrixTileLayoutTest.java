package io.castles.core.tile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MatrixTileLayoutTest {

    Matrix<TileContent> matrix = new Matrix<>(
            3,
            3,
            new TileContent[]{
                    TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                    TileContent.STREET, TileContent.STREET, TileContent.STREET,
                    TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE
            });

    static Stream<Arguments> directionAndExpected() {
        return Stream.of(
                Arguments.of(TileLayout.LEFT, List.of(TileContent.GRAS, TileContent.STREET, TileContent.CASTLE)),
                Arguments.of(TileLayout.RIGHT, List.of(TileContent.GRAS, TileContent.STREET, TileContent.CASTLE)),
                Arguments.of(TileLayout.TOP, List.of(TileContent.GRAS, TileContent.GRAS, TileContent.GRAS)),
                Arguments.of(TileLayout.BOTTOM, List.of(TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE))
        );
    }

    @ParameterizedTest
    @MethodSource("directionAndExpected")
    void shouldGetMatrixEdges(int direction, List<TileContent> expectedEdge) {
        var tileLayout = new MatrixTileLayout(matrix);
        assertThat(tileLayout.getTileContentEdge(direction)).containsExactlyElementsOf(expectedEdge);
    }

    @Test
    void shouldBeEqualToSimilarMatrixTileLayout() {
        assertThat(new MatrixTileLayout(matrix)).isEqualTo(new MatrixTileLayout(matrix));
    }

    // TODO: this could be an abstract test for `AbstractTileLayout`
    @Test
    void shouldRotateCorrectly() {
        var tileLayout = new MatrixTileLayout(matrix);
        tileLayout.rotate();

        assertThat(tileLayout.getTileContentEdge(TileLayout.LEFT)).containsExactlyElementsOf(List.of(TileContent.GRAS, TileContent.GRAS, TileContent.GRAS));
        assertThat(tileLayout.getTileContentEdge(TileLayout.RIGHT)).containsExactlyElementsOf(List.of(TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE));
        assertThat(tileLayout.getTileContentEdge(TileLayout.TOP)).containsExactlyElementsOf(List.of(TileContent.GRAS, TileContent.STREET, TileContent.CASTLE));
        assertThat(tileLayout.getTileContentEdge(TileLayout.BOTTOM)).containsExactlyElementsOf(List.of(TileContent.GRAS, TileContent.STREET, TileContent.CASTLE));

        tileLayout.rotate();

        assertThat(tileLayout.getTileContentEdge(TileLayout.LEFT)).containsExactlyElementsOf(List.of(TileContent.GRAS, TileContent.STREET, TileContent.CASTLE));
        assertThat(tileLayout.getTileContentEdge(TileLayout.RIGHT)).containsExactlyElementsOf(List.of(TileContent.GRAS, TileContent.STREET, TileContent.CASTLE));
        assertThat(tileLayout.getTileContentEdge(TileLayout.TOP)).containsExactlyElementsOf(List.of(TileContent.CASTLE, TileContent.CASTLE, TileContent.CASTLE));
        assertThat(tileLayout.getTileContentEdge(TileLayout.BOTTOM)).containsExactlyElementsOf(List.of(TileContent.GRAS, TileContent.GRAS, TileContent.GRAS));
    }

    @Test
    void shouldDoMultipleRotatesCorrectly() {
        var tileLayout = new MatrixTileLayout(matrix);
        tileLayout.rotate();
        tileLayout.rotate();
        tileLayout.rotate();

        MatrixTileLayout other = new MatrixTileLayout(matrix);
        other.rotate(3);
        assertThat(tileLayout).isEqualTo(other);
    }

}