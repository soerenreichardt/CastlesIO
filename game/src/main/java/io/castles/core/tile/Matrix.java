package io.castles.core.tile;

import lombok.Value;

@Value
public class Matrix<T> {

    int rows;
    int columns;
    T[] values;

    public Matrix(int rows, int columns, T[] values) {
        this.rows = rows;
        this.columns = columns;

        assert values.length == rows * columns;
        this.values = values;
    }

    public T get(int row, int column) {
        return values[column + columns * row];
    }

    public int getRowFromIndex(int index) {
        return index / columns;
    }

    public int getColumnFromIndex(int index) {
        return index % columns;
    }
}
