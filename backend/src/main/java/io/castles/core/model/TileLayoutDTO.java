package io.castles.core.model;

import io.castles.core.tile.TileLayout;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TileLayoutDTO {
    List<TileLayout.PositionedContent> layout;
    int rotation;

    public TileLayout toTileLayout() {
        return new TileLayout(layout, rotation);
    }

    public static TileLayoutDTO from(TileLayout tileLayout) {
        return new TileLayoutDTO(tileLayout.getLayout(), tileLayout.getRotation());
    }
}
