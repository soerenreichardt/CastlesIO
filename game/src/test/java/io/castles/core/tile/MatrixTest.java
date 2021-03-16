package io.castles.core.tile;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MatrixTest {

    Matrix<Integer> integerMatrix = new Matrix<>(2, 2, new Integer[]{1, 2, 3, 4});

    @Test
    void shouldGetCorrectCells() {
        assertEquals(1, integerMatrix.get(0, 0));
        assertEquals(2, integerMatrix.get(0, 1));
        assertEquals(3, integerMatrix.get(1, 0));
        assertEquals(4, integerMatrix.get(1, 1));
    }

    @Test
    void shouldEqualSimilarMatrix() {
        assertThat(integerMatrix).isEqualTo(new Matrix<>(2, 2, new Integer[]{1, 2, 3, 4}));
    }

    @Test
    void shouldComputeRowAndColumnBasedOnIndex() {
        Matrix<Integer> matrix = new Matrix<>(3, 3, new Integer[]{
                0, 1, 2,
                3, 4, 5,
                6, 7, 8
        });
        assertThat(matrix.getColumnFromIndex(3)).isEqualTo(0);
        assertThat(matrix.getRowFromIndex(3)).isEqualTo(1);

        assertThat(matrix.getColumnFromIndex(7)).isEqualTo(1);
        assertThat(matrix.getRowFromIndex(7)).isEqualTo(2);
    }

}