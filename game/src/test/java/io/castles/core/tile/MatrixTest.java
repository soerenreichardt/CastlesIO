package io.castles.core.tile;

import org.junit.jupiter.api.Test;

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

}