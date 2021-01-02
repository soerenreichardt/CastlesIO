package io.castles.core.model;

import io.castles.core.tile.TileLayout;
import lombok.Value;

import java.util.List;

@Value
public class TileLayoutDTO {
    List<TileLayout.PositionedContent> tileLayout;
    int rotation;

    public TileLayout toTileLayout() {
        return new TileLayout(tileLayout, rotation);
    }

    public static TileLayoutDTO from(TileLayout tileLayout) {
        return new TileLayoutDTO(tileLayout.getContent(), tileLayout.getRotation());
    }
}
