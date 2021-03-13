package io.castles.core.tile;

import org.assertj.core.api.AbstractBooleanAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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
    void shouldGetCenterContent() {
        var tileLayout = new MatrixTileLayout(matrix);
        assertThat(tileLayout.getCenter()).isEqualTo(TileContent.STREET);
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

        var other = new MatrixTileLayout(matrix);
        other.rotate(3);
        assertThat(tileLayout).isEqualTo(other);

        var negativeRotationLayout = new MatrixTileLayout(matrix);
        negativeRotationLayout.rotate(-1);
        assertThat(other).isEqualTo(negativeRotationLayout);
    }

    @ParameterizedTest
    @CsvSource({ "0, true", "1, false", "2, true", "3, false" })
    void shouldMatchOtherTile(int direction, boolean shouldMatch) {
        var tileLayout = new MatrixTileLayout(matrix);
        var otherTileLayout = new MatrixTileLayout(matrix);

        AbstractBooleanAssert<?> booleanAssert = assertThat(tileLayout.matches(otherTileLayout, direction));
        if (shouldMatch) {
            booleanAssert.isTrue();
        } else {
            booleanAssert.isFalse();
        }
    }

    @Test
    void shouldMatchOtherTileWithRotation() {
        var tileLayout = new MatrixTileLayout(matrix);
        var otherTileLayout = new MatrixTileLayout(matrix);

        for (int i = 0; i < TileLayout.NUM_EDGES; i++) {
            tileLayout.rotate(1);
            otherTileLayout.rotate(-1);

            assertThat(tileLayout.matches(otherTileLayout, TileLayout.RIGHT)).isTrue();
        }
    }

    @Test
    void shouldCreateTileLayoutWithBuilder() {
        var uniformLayout = MatrixTileLayout.builder().setAll(TileContent.GRAS);
        assertThat(uniformLayout).isEqualTo(new MatrixTileLayout(new Matrix<>(1, 1, new TileContent[]{TileContent.GRAS})));

        var matrixTileLayout = MatrixTileLayout.builder()
                .setBackground(TileContent.GRAS)
                .setLeftEdge(TileContent.CASTLE)
                .setRightEdge(TileContent.CASTLE)
                .build();

        assertThat(matrixTileLayout.getCenter()).isEqualTo(TileContent.GRAS);
        assertThat(matrixTileLayout.getTileContentEdge(TileLayout.LEFT)).allMatch(content -> content == TileContent.CASTLE);
        assertThat(matrixTileLayout.getTileContentEdge(TileLayout.RIGHT)).allMatch(content -> content == TileContent.CASTLE);
    }
}