package io.castles.core.model.dto;

import io.castles.core.tile.Tile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

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
        return new TileDTO(
                tile.getId(),
                TileLayoutDTO.from(tile.getTileLayout())
        );
    }

    public static TileDTO from(Optional<Tile> tile) {
        return tile.map(TileDTO::from).orElse(null);
    }
}
