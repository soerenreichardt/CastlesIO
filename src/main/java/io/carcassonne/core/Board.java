package io.carcassonne.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Board {

    Map<Integer, Map<Integer, Tile>> tiles;

    public Board() {
        this.tiles = new HashMap<>();
        setInitialTile();
    }

    public Tile getTile(int x, int y) {
        if (tiles.containsKey(x)) {
            var innerTiles = tiles.get(x);
            if (innerTiles.containsKey(y)) {
                return innerTiles.get(y);
            }
        }
        throw new IllegalArgumentException(String.format("No tile was found at position (%d|%d).", x, y));
    }

    public boolean insertTileToBoard(int x, int y, Tile tile) {
        var innerTiles = tiles.computeIfAbsent(x, ignore -> new HashMap<>());
        if (innerTiles.containsKey(y)) {
            return false;
        }

        tile.insertToBoard(x, y);

        if (!setNeighborsFromAndToTile(tile)) {
            // TODO remove tile from board
            throw new IllegalArgumentException(
                    String.format(
                            "A tile needs at least one neighbor but no neighbors were found at position (%d|%d).",
                            x,
                            y
                    )
            );
        }

        innerTiles.put(y, tile);

        return true;
    }

    private boolean setNeighborsFromAndToTile(Tile tile) {
        int x = tile.getX();
        int y = tile.getY();

        boolean hasNeighbors;
        hasNeighbors = getLeftNeighbor(x, y).map(neighbor -> {
            neighbor.setNeighbor(Tile.RIGHT, tile);
            tile.setNeighbor(Tile.LEFT, neighbor);
            return true;
        }).isPresent();

        hasNeighbors |= getRightNeighbor(x, y).map(neighbor -> {
            neighbor.setNeighbor(Tile.LEFT, tile);
            tile.setNeighbor(Tile.RIGHT, neighbor);
            return true;
        }).isPresent();

        hasNeighbors |= getTopNeighbor(x, y).map(neighbor -> {
            neighbor.setNeighbor(Tile.BOTTOM, tile);
            tile.setNeighbor(Tile.TOP, neighbor);
            return true;
        }).isPresent();

        hasNeighbors |= getBottomNeighbor(x, y).map(neighbor -> {
            neighbor.setNeighbor(Tile.TOP, tile);
            tile.setNeighbor(Tile.BOTTOM, neighbor);
            return true;
        }).isPresent();

        return hasNeighbors;
    }

    private void setInitialTile() {
        var tile = Tile.drawRandom();
        tile.insertToBoard(0, 0);

        this.tiles
                .computeIfAbsent(tile.getX(), ignore -> new HashMap<>())
                .put(tile.getY(), tile);
    }

    private Optional<Tile> getLeftNeighbor(int x, int y) {
        return getNeighbors(x - 1, y);
    }

    private Optional<Tile> getRightNeighbor(int x, int y) {
        return getNeighbors(x + 1, y);
    }

    private Optional<Tile> getTopNeighbor(int x, int y) {
        return getNeighbors(x, y + 1);
    }

    private Optional<Tile> getBottomNeighbor(int x, int y) {
        return getNeighbors(x, y - 1);
    }

    private Optional<Tile> getNeighbors(int x, int y) {
        if (tiles.containsKey(x)) {
            var innerTiles = tiles.get(x);
            if (innerTiles.containsKey(y)) {
                return Optional.of(innerTiles.get(y));
            }
        }
        return Optional.empty();
    }
}
