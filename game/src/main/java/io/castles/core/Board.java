package io.castles.core;

import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.core.tile.TileLayoutImpl;

import java.util.*;

public class Board {

    private final Map<Integer, Map<Integer, Tile>> tiles;
    private final TileProducer tileProducer;

    public static Board create(GameMode gameMode, Optional<List<Tile>> tileList) {
        if (gameMode == GameMode.DEBUG) {
            return Board.withStaticTile(TileContent.GRAS);
        }
        if (gameMode == GameMode.ORIGINAL) {
            List<Tile> tiles = tileList.orElseThrow(
                    () -> new IllegalStateException(String.format("No list of tiles was specified for game mode %s", gameMode))
            );
            return Board.withPredefinedTiles(tiles);
        }
        if (gameMode == GameMode.RANDOM) {
            return Board.withRandomTile();
        }

        throw new IllegalArgumentException("This should never happen. I miss exhaustiveness checks :(");
    }

    public static Board withPredefinedTiles(List<Tile> tiles) {
        var rng = new Random();
        return new Board(() -> tiles.get(rng.nextInt(tiles.size())));
    }

    public static Board withStaticTile(TileContent border) {
        return new Board(() -> Tile.drawStatic(border));
    }

    public static Board withRandomTile() {
        return new Board(Tile::drawRandom);
    }

    private Board(TileProducer tileProducer) {
        this.tiles = new HashMap<>();
        this.tileProducer = tileProducer;
        setInitialTile(tileProducer.get());
    }

    public Tile getNewTile() {
        return tileProducer.get();
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

    public void insertTileToBoard(Tile tile, int x, int y) {
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

        neighbors[TileLayoutImpl.LEFT] = getLeftNeighbor(x, y).orElse(null);
        neighbors[TileLayoutImpl.RIGHT] = getRightNeighbor(x, y).orElse(null);
        neighbors[TileLayoutImpl.TOP] = getTopNeighbor(x, y).orElse(null);
        neighbors[TileLayoutImpl.BOTTOM] = getBottomNeighbor(x, y).orElse(null);

        return neighbors;
    }

    private void checkTileHasNeighbor(Tile tile, Tile[] neighbors) {
        boolean hasNoNeighbor = neighbors[TileLayoutImpl.LEFT] == null
                && neighbors[TileLayoutImpl.RIGHT] == null
                && neighbors[TileLayoutImpl.TOP] == null
                && neighbors[TileLayoutImpl.BOTTOM] == null;

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
        boolean leftMatching = tile.matches(neighbors[TileLayoutImpl.LEFT], TileLayoutImpl.LEFT);
        boolean rightMatching = tile.matches(neighbors[TileLayoutImpl.RIGHT], TileLayoutImpl.RIGHT);
        boolean topMatching = tile.matches(neighbors[TileLayoutImpl.TOP], TileLayoutImpl.TOP);
        boolean bottomMatching = tile.matches(neighbors[TileLayoutImpl.BOTTOM], TileLayoutImpl.BOTTOM);

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
        Tile leftNeighbor = neighbors[TileLayoutImpl.LEFT];
        if (leftNeighbor != null) {
            leftNeighbor.setNeighbor(TileLayoutImpl.RIGHT, tile);
            tile.setNeighbor(TileLayoutImpl.LEFT, leftNeighbor);
        }

        Tile rightNeighbor = neighbors[TileLayoutImpl.RIGHT];
        if (rightNeighbor != null) {
            rightNeighbor.setNeighbor(TileLayoutImpl.LEFT, tile);
            tile.setNeighbor(TileLayoutImpl.RIGHT, rightNeighbor);
        }

        Tile topNeighbor = neighbors[TileLayoutImpl.TOP];
        if (topNeighbor != null) {
            topNeighbor.setNeighbor(TileLayoutImpl.BOTTOM, tile);
            tile.setNeighbor(TileLayoutImpl.TOP, topNeighbor);
        }

        Tile bottomNeighbor = neighbors[TileLayoutImpl.BOTTOM];
        if (bottomNeighbor != null) {
            bottomNeighbor.setNeighbor(TileLayoutImpl.TOP, tile);
            tile.setNeighbor(TileLayoutImpl.BOTTOM, bottomNeighbor);
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

    interface TileProducer {
        Tile get();
    }
}
