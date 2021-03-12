package io.castles.core.model;

import io.castles.core.tile.TileLayoutImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TileLayoutDTO {
    List<TileLayoutImpl.PositionedContent> layout;
    int rotation;

    public TileLayoutImpl toTileLayout() {
        return new TileLayoutImpl(layout, rotation);
    }

    public static TileLayoutDTO from(TileLayoutImpl tileLayout) {
        return new TileLayoutDTO(tileLayout.getLayout(), tileLayout.getRotation());
    }
}
