package io.castles.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Board {

    Map<Integer, Map<Integer, Tile>> tiles;

    public static Board withStaticTile(Tile.TileBorder border) {
        return new Board(Tile.drawStatic(border));
    }

    public static Board withRandomTile() {
        return new Board(Tile.drawRandom());
    }

    private Board(Tile initialTile) {
        this.tiles = new HashMap<>();
        setInitialTile(initialTile);
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

    public void insertTileToBoard(int x, int y, Tile tile) {
        var innerTiles = tiles.computeIfAbsent(x, ignore -> new HashMap<>());
        if (innerTiles.containsKey(y)) {
            throw new IllegalArgumentException(String.format("Position (%d|%d) is already occupied", x, y));
        }

        tile.insertToBoard(x, y);

        Tile[] neighbors = getNeighborsOfTile(tile);

        checkTileHasNeighbor(tile, neighbors);
        checkMatchingBorders(tile, neighbors);

        setNeighborsFromAndToTile(tile, neighbors);

        innerTiles.put(y, tile);
    }

    private Tile[] getNeighborsOfTile(Tile tile) {
        int x = tile.getX();
        int y = tile.getY();

        Tile[] neighbors = new Tile[4];

        neighbors[Tile.LEFT] = getLeftNeighbor(x, y).orElse(null);
        neighbors[Tile.RIGHT] = getRightNeighbor(x, y).orElse(null);
        neighbors[Tile.TOP] = getTopNeighbor(x, y).orElse(null);
        neighbors[Tile.BOTTOM] = getBottomNeighbor(x, y).orElse(null);

        return neighbors;
    }

    private void checkTileHasNeighbor(Tile tile, Tile[] neighbors) {
        boolean hasNoNeighbor = neighbors[Tile.LEFT] == null
                && neighbors[Tile.RIGHT] == null
                && neighbors[Tile.TOP] == null
                && neighbors[Tile.BOTTOM] == null;

        if (hasNoNeighbor) {
            throw new IllegalArgumentException(
                    String.format(
                            "A tile needs at least one neighbor but no neighbors were found at position (%d|%d).",
                            tile.getX(),
                            tile.getY()
                    )
            );
        }
    }

    private void checkMatchingBorders(Tile tile, Tile[] neighbors) {
        boolean leftMatching = tile.matches(neighbors[Tile.LEFT], Tile.LEFT);
        boolean rightMatching = tile.matches(neighbors[Tile.RIGHT], Tile.RIGHT);
        boolean topMatching = tile.matches(neighbors[Tile.TOP], Tile.TOP);
        boolean bottomMatching = tile.matches(neighbors[Tile.BOTTOM], Tile.BOTTOM);

        if (!(leftMatching && rightMatching && topMatching && bottomMatching)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Tile at position (%d|%d) has incompatible borders to its surrounding.",
                            tile.getX(),
                            tile.getY()
                    )
            );
        }
    }

    private void setNeighborsFromAndToTile(Tile tile, Tile[] neighbors) {
        Tile leftNeighbor = neighbors[Tile.LEFT];
        if (leftNeighbor != null) {
            leftNeighbor.setNeighbor(Tile.RIGHT, tile);
            tile.setNeighbor(Tile.LEFT, leftNeighbor);
        }

        Tile rightNeighbor = neighbors[Tile.RIGHT];
        if (rightNeighbor != null) {
            rightNeighbor.setNeighbor(Tile.LEFT, tile);
            tile.setNeighbor(Tile.RIGHT, rightNeighbor);
        }

        Tile topNeighbor = neighbors[Tile.TOP];
        if (topNeighbor != null) {
            topNeighbor.setNeighbor(Tile.BOTTOM, tile);
            tile.setNeighbor(Tile.TOP, topNeighbor);
        }

        Tile bottomNeighbor = neighbors[Tile.BOTTOM];
        if (bottomNeighbor != null) {
            bottomNeighbor.setNeighbor(Tile.TOP, tile);
            tile.setNeighbor(Tile.BOTTOM, bottomNeighbor);
        }
    }

    private void setInitialTile(Tile tile) {
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
