package io.castles.core.model;

import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TileDTO {
    UUID id;
    TileContent[] tileContents;

    public Tile toTile() {
        return new Tile(id, tileContents);
    }
}
