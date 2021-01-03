package io.castles.core.model;

import io.castles.core.tile.Tile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileDTO {
    long id;
    TileLayoutDTO tileLayout;

    public Tile toTile() {
        return new Tile(id, tileLayout.toTileLayout());
    }

    public static TileDTO from(Tile tile) {
        return new TileDTO(tile.getId(), TileLayoutDTO.from(tile.getTileLayout()));
    }
}
