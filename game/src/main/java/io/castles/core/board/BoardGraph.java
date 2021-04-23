package io.castles.core.board;

import io.castles.core.graph.Graph;
import io.castles.core.graph.algorithm.GraphBfs;
import io.castles.core.graph.algorithm.Wcc;
import io.castles.core.tile.Figure;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.exceptions.RegionOccupiedException;
import lombok.Value;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BoardGraph implements BoardListener {

    public static final int UNCLOSED_STREET = -1;
    public static final Set<TileContent> VALID_FIGURE_REGIONS = Set.of(TileContent.GRAS, TileContent.CASTLE);

    private final List<Graph> graphs;
    private final TileLookup tileLookup;

    public BoardGraph(TileLookup tileLookup) {
        this.tileLookup = tileLookup;
        this.graphs = new ArrayList<>();
    }

    @Override
    public void onTileAdded(Tile tile) {
        graphs.forEach(graph -> graph.fromTile(tile));
    }

    @Override
    public void currentState(Map<Integer, Map<Integer, Tile>> board) {
        List<Tile> tiles = board.entrySet()
                .stream()
                .flatMap(inner -> inner.getValue().entrySet().stream())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        graphs.forEach(graph -> graph.fromExistingBoard(tiles));
    }

    @Override
    public void initialize() {
        graphs.add(new Graph(TileContent.GRAS));
        graphs.add(new Graph(TileContent.CASTLE));
        graphs.add(new Graph(TileContent.STREET));
    }

    @Override
    public void restart() {
        graphs.clear();
        initialize();
    }

    public Optional<Graph> getGraphThatContainsNode(int x, int y, int row, int column) {
        var node = new Graph.Node(x, y, row, column);
        return graphs
                .stream()
                .filter(graph -> graph.nodes().contains(node))
                .findFirst();
    }

    public int getStreetLength(Tile tile, int row, int column) {
        Graph graph = filterGraphsForContent(TileContent.STREET);

        GraphBfs graphBfs = new GraphBfs(graph);
        Graph.Node startNode = new Graph.Node(tile.getX(), tile.getY(), row, column);
        if (!graph.nodes().contains(startNode)) {
            throw new IllegalArgumentException(String.format("No node %s was found in graph.", startNode));
        }
        Set<Position> distinctTileIds = new HashSet<>();
        AtomicBoolean closedStreet = new AtomicBoolean(true);
        graphBfs.compute(startNode, (node, neighbors) -> {
            distinctTileIds.add(new Position(node.getX(), node.getY()));

            // The neighbors of a street end have to be empty,
            // otherwise the traversal should go on
            if (neighbors.isEmpty()) {
                // Street ends are always in the middle of a tile
                if (!nodeEndsInMiddleOfTile(node, node.getX(), node.getY())) {
                    closedStreet.set(false);
                }
            }
            return true;
        });

        return closedStreet.get()
            ? distinctTileIds.size()
            : UNCLOSED_STREET;
    }

    public void validateFigurePlacement(Figure figure, Collection<Figure> existingFigures) throws RegionOccupiedException {
        var figurePosition = figure.getPosition();
        var graphContainingFigure = getGraphThatContainsNode(figurePosition.getX(), figurePosition.getY(), figurePosition.getRow(), figurePosition.getColumn());
        if (graphContainingFigure.isEmpty()) {
            throw new IllegalArgumentException(String.format("Tile region type needs to be one of [%s]", VALID_FIGURE_REGIONS.stream().map(TileContent::name).collect(Collectors.joining(", "))));
        }

        validateUniqueFigurePositionInWcc(graphContainingFigure.get(), figure, existingFigures);
    }

    private void validateUniqueFigurePositionInWcc(Graph graph, Figure figureToPlace, Collection<Figure> existingFigures) throws RegionOccupiedException {
        var wcc = new Wcc(graph);
        wcc.compute();

        for (var existingFigure : existingFigures) {
            if (wcc.sameComponent(figureToPlace.getPosition(), existingFigure.getPosition())) {
                throw new RegionOccupiedException(graph.tileContent());
            }
        }
    }

    private Graph filterGraphsForContent(TileContent tileContent) {
        return graphs.stream()
                .filter(g -> g.tileContent() == tileContent)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No graph was found for TileContent %s", TileContent.STREET)));
    }

    private boolean nodeEndsInMiddleOfTile(Graph.Node node, int x, int y) {
        var sinkTile = tileLookup.resolve(x, y);
        var tileMatrix = sinkTile.<MatrixTileLayout>getTileLayout().getContent();
        var streetEndsOnEdge = node.getColumn() == 0
                || node.getRow() == 0
                || node.getRow() == tileMatrix.getRows() - 1
                || node.getColumn() == tileMatrix.getColumns() - 1;
        return !streetEndsOnEdge;
    }

    public interface TileLookup {
        Tile resolve(int x, int y);
    }

    @Value
    static class Position {
        int x;
        int y;
    }
}
