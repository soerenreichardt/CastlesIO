package io.castles.core.model;

import io.castles.core.Tile;
import io.castles.core.Tile.TileBorder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileDTO {
    UUID id;
    TileBorder[] tileBorders;

    public Tile toTile() {
        return new Tile(id, tileBorders);
    }
}
