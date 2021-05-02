package io.castles.core.board;

import io.castles.core.GameMode;
import io.castles.core.board.TileIterators.TileIterator;
import io.castles.core.tile.Figure;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.core.tile.TileLayout;
import io.castles.exceptions.RegionOccupiedException;
import io.castles.game.Lifecycle;
import org.jetbrains.annotations.TestOnly;

import java.util.*;

public class Board implements Lifecycle {

    private final Map<Integer, Map<Integer, Tile>> tiles;
    private final TileIterator tileIterator;
    private final List<BoardListener> boardListeners;
    private final BoardGraph boardGraph;
    private final List<Figure> figures;

    public static Board create(GameMode gameMode, List<Tile> tileList) {
        if (gameMode == GameMode.DEBUG) {
            return Board.withStaticTile(TileContent.GRAS);
        }
        if (gameMode == GameMode.ORIGINAL) {
            if (tileList.isEmpty()) {
                throw new IllegalStateException(String.format("No list of tiles was specified for game mode %s", gameMode));
            }
            return Board.withPredefinedTiles(tileList);
        }

        throw new IllegalArgumentException("This should never happen. I miss exhaustiveness checks :(");
    }

    public static Board withPredefinedTiles(List<Tile> tiles) {
        return new Board(new TileIterators.RandomList(tiles));
    }

    @TestOnly
    public static Board withStaticTile(TileContent border) {
        return new Board(new TileIterators.Static(Tile.drawStatic(border)));
    }

    @TestOnly
    public static Board withSpecificTile(TileLayout layout) {
        return new Board(new TileIterators.Static(new Tile(layout)));
    }

    private Board(TileIterator tileIterator) {
        this.tiles = new HashMap<>();
        this.tileIterator = tileIterator;
        this.boardListeners = new ArrayList<>();
        this.boardGraph = new BoardGraph(this::getTileOrNull);
        this.figures = new ArrayList<>();

        initialize();
    }

    @Override
    public void initialize() {
        addBoardListener(boardGraph);
        if (!tileIterator.hasNext()) {
            throw new RuntimeException("Could not draw initial tile.");
        }
        setInitialTile(tileIterator.next());
    }

    @Override
    public void restart() {
        tiles.clear();
        boardListeners.forEach(BoardListener::restart);
        boardListeners.remove(boardGraph);
        initialize();
    }

    public BoardGraph getBoardGraph() {
        return this.boardGraph;
    }

    public boolean hasNextTile() {
        return tileIterator.hasNext();
    }

    public Tile getNextTile() {
        return tileIterator.next();
    }

    public int getNumTilesLeft() {
        return tileIterator.getNumTilesLeft();
    }

    public Tile getTile(int x, int y) {
        var tileOrNull = getTileOrNull(x, y);
        if (tileOrNull != null) {
            return tileOrNull;
        }
        throw new IllegalArgumentException(String.format("No tile was found at position (%d|%d).", x, y));
    }

    private Tile getTileOrNull(int x, int y) {
        if (tiles.containsKey(x)) {
            var innerTiles = tiles.get(x);
            if (innerTiles.containsKey(y)) {
                return innerTiles.get(y);
            }
        }
        return null;
    }

    public Map<Integer, Map<Integer, Tile>> getTiles() {
        return this.tiles;
    }

    public List<Figure> getFigures() {
        return this.figures;
    }

    public void insertTileToBoard(Tile tile, int x, int y) {
        var innerTiles = tiles.computeIfAbsent(x, ignore -> new HashMap<>());
        if (innerTiles.containsKey(y)) {
            throw new IllegalArgumentException(String.format("Position (%d|%d) is already occupied", x, y));
        }

        tile.insertToBoard(x, y);

        Tile[] neighbors = getNeighborsOfPosition(tile.getX(), tile.getY());

        validateHasNeighbors(tile, neighbors);
        validateMatchingNeighbors(tile, neighbors);

        setNeighborsFromAndToTile(tile, neighbors);

        innerTiles.put(y, tile);
        notifyListeners(tile);
    }

    public List<Integer> getMatchingRotations(Tile tile, int x, int y) {
        List<Integer> matchingRotations = new ArrayList<>();

        for(var i = 0; i <= 3; i++) {
            Tile[] neighbors = getNeighborsOfPosition(x, y);
            if (tileMatchesNeighbors(tile, neighbors)) {
                matchingRotations.add(i);
            }
            tile.rotate();
        }

        return matchingRotations;
    };

    public void addBoardListener(BoardListener listener) {
        this.boardListeners.add(listener);
        listener.initialize();
        listener.currentState(tiles);
    }

    public void placeFigureOnTile(Figure figure) throws RegionOccupiedException {
        getBoardGraph().validateFigurePlacement(figure, figures);
        figures.add(figure);
    }

    private void validateHasNeighbors(Tile tile, Tile[] neighbors) {
        if (!hasNeighbors(neighbors)) {
            throw new IllegalArgumentException(
                    String.format(
                            "A tile needs at least one neighbor but no neighbors were found at position (%d|%d).",
                            tile.getX(),
                            tile.getY()
                    )
            );
        }
        ;
    }

    private void validateMatchingNeighbors(Tile tile, Tile[] neighbors) {
        if (!tileMatchesNeighbors(tile, neighbors)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Tile at position (%d|%d) has incompatible borders to its surrounding.",
                            tile.getX(),
                            tile.getY()
                    )
            );
        }
    }

    private Tile[] getNeighborsOfPosition(int x, int y) {
        Tile[] neighbors = new Tile[4];

        neighbors[TileLayout.LEFT] = getLeftNeighbor(x, y).orElse(null);
        neighbors[TileLayout.RIGHT] = getRightNeighbor(x, y).orElse(null);
        neighbors[TileLayout.TOP] = getTopNeighbor(x, y).orElse(null);
        neighbors[TileLayout.BOTTOM] = getBottomNeighbor(x, y).orElse(null);

        return neighbors;
    }

    private boolean hasNeighbors(Tile[] neighbors) {
        boolean hasNoNeighbor = neighbors[TileLayout.LEFT] == null
                && neighbors[TileLayout.RIGHT] == null
                && neighbors[TileLayout.TOP] == null
                && neighbors[TileLayout.BOTTOM] == null;

        return !hasNoNeighbor;
    }

    private boolean tileMatchesNeighbors(Tile tile, Tile[] neighbors) {
        boolean leftMatching = tile.matches(neighbors[TileLayout.LEFT], TileLayout.LEFT);
        boolean rightMatching = tile.matches(neighbors[TileLayout.RIGHT], TileLayout.RIGHT);
        boolean topMatching = tile.matches(neighbors[TileLayout.TOP], TileLayout.TOP);
        boolean bottomMatching = tile.matches(neighbors[TileLayout.BOTTOM], TileLayout.BOTTOM);

        return (leftMatching && rightMatching && topMatching && bottomMatching);
    }

    private void setNeighborsFromAndToTile(Tile tile, Tile[] neighbors) {
        Tile leftNeighbor = neighbors[TileLayout.LEFT];
        if (leftNeighbor != null) {
            leftNeighbor.setNeighbor(TileLayout.RIGHT, tile);
            tile.setNeighbor(TileLayout.LEFT, leftNeighbor);
        }

        Tile rightNeighbor = neighbors[TileLayout.RIGHT];
        if (rightNeighbor != null) {
            rightNeighbor.setNeighbor(TileLayout.LEFT, tile);
            tile.setNeighbor(TileLayout.RIGHT, rightNeighbor);
        }

        Tile topNeighbor = neighbors[TileLayout.TOP];
        if (topNeighbor != null) {
            topNeighbor.setNeighbor(TileLayout.BOTTOM, tile);
            tile.setNeighbor(TileLayout.TOP, topNeighbor);
        }

        Tile bottomNeighbor = neighbors[TileLayout.BOTTOM];
        if (bottomNeighbor != null) {
            bottomNeighbor.setNeighbor(TileLayout.TOP, tile);
            tile.setNeighbor(TileLayout.BOTTOM, bottomNeighbor);
        }
    }

    private void setInitialTile(Tile tile) {
        tile.insertToBoard(0, 0);

        this.tiles
                .computeIfAbsent(tile.getX(), ignore -> new HashMap<>())
                .put(tile.getY(), tile);
        notifyListeners(tile);
    }

    private void notifyListeners(Tile tile) {
        this.boardListeners.forEach(boardListener -> boardListener.onTileAdded(tile));
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
