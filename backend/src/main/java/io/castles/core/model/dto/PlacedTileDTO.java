package io.castles.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlacedTileDTO {
    TileDTO tile;
    int x;
    int y;
}
