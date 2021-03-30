package io.castles.core.model.dto;

import io.castles.core.tile.Matrix;
import io.castles.core.tile.TileContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TileContentMatrixDTO {
    int rows;
    int columns;
    TileContent[] values;

    public Matrix<TileContent> toTileContentMatrix() {
        return new Matrix<>(rows, columns, values);
    }

    public static TileContentMatrixDTO from(Matrix<TileContent> tileContentMatrix) {
        return new TileContentMatrixDTO(tileContentMatrix.getRows(), tileContentMatrix.getColumns(), tileContentMatrix.getValues());
    }
}
