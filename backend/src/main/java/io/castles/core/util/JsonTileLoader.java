package io.castles.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.castles.core.model.dto.TileDTO;
import io.castles.core.tile.Tile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JsonTileLoader {

    static final String TILES_JSON_PATH = "predefined/tiles.json";

    private final ObjectMapper objectMapper;

    public JsonTileLoader() {
        this.objectMapper = new ObjectMapper();
    }

    public List<Tile> getTilesFromResource() throws IOException {
        return getTilesFromResource(TILES_JSON_PATH);
    }

    public List<Tile> getTilesFromResource(String filePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        TileDTO[] tiles = this.objectMapper.readValue(inputStream, TileDTO[].class);
        return Arrays.stream(tiles).map(TileDTO::toTile).collect(Collectors.toList());
    }
}
