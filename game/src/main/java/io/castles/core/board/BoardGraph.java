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

    public Set<Set<Graph.Node>> closedCastles(Tile tile) {
        var castleGraph = filterGraphsForContent(TileContent.CASTLE);
        var closedCastlesTracker = new ClosedCastlesTracker(castleGraph, tileLookup);
        var closedCastleNodes = closedCastlesTracker.closedCastleNodes(tile);

        var graphBfs = new GraphBfs(castleGraph);
        Set<Set<Graph.Node>> closedCastles = new HashSet<>();
        for (Graph.Node closedCastleNode : closedCastleNodes) {
            Set<Graph.Node> closedCastleComponent = new HashSet<>();
            closedCastles.add(closedCastleComponent);
            graphBfs.compute(closedCastleNode, (node, neighbors) -> closedCastleComponent.add(node));
        }
        return closedCastles;
    }

    public Set<Set<Graph.Node>> closedStreets(Tile tile) {
        Graph graph = filterGraphsForContent(TileContent.STREET);

        var closedCastlesTracker = new ClosedCastlesTracker(graph, tileLookup);
        var closedStreetNodes = closedCastlesTracker.closedCastleNodes(tile);
        var graphBfs = new GraphBfs(graph);
        Set<Set<Graph.Node>> closedStreets = new HashSet<>();
        for (Graph.Node closedStreetNode : closedStreetNodes) {
            Set<Graph.Node> closedCastleComponent = new HashSet<>();
            closedStreets.add(closedCastleComponent);
            graphBfs.compute(closedStreetNode, (node, neighbors) -> closedCastleComponent.add(node));
        }
        return closedStreets;
    }

    public int distinctTilesInNodeSet(Set<Graph.Node> nodes) {
        return nodes.stream()
                .map(node -> tileLookup.resolve(node.getX(), node.getY()))
                .collect(Collectors.toSet())
                .size();
    }

    public int getStreetLength(Tile tile) {
        var closedStreets = closedStreets(tile);

        return closedStreets.isEmpty()
                ? UNCLOSED_STREET
                : distinctTilesInNodeSet(closedStreets.iterator().next());
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
