package io.castles.core.model;

import io.castles.core.tile.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TileLayoutDTO {
    TileContentMatrixDTO content;
    int rotation;

    public MatrixTileLayout toTileLayout() {
        return MatrixTileLayout.createWithRotation(content.toTileContentMatrix(), rotation);
    }

    public static TileLayoutDTO from(MatrixTileLayout tileLayout) {
        return new TileLayoutDTO(TileContentMatrixDTO.from(tileLayout.getContent()), tileLayout.getRotation());
    }
}
