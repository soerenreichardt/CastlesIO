package io.castles.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class TilePlacedDTO {
    PlacedTileDTO placedTile;
    int tilesLeft;
}
