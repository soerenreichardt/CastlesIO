package io.castles.core.board;

import io.castles.core.graph.Graph;
import io.castles.core.graph.algorithm.GraphBfs;
import io.castles.core.graph.algorithm.Wcc;
import io.castles.core.tile.Figure;
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
    private final List<BoardGraphEventCallback> eventCallbacks;

    private final Set<Set<Graph.Node>> closedCastles;

    public BoardGraph(TileLookup tileLookup) {
        this.tileLookup = tileLookup;
        this.graphs = new ArrayList<>();
        this.eventCallbacks = new ArrayList<>();
        this.closedCastles = new HashSet<>();
    }

    @Override
    public void onTileAdded(Tile tile) {
        graphs.forEach(graph -> graph.fromTile(tile));
        var closedCastles = closedCastles(tile);
        var closedStreets = closedStreets(tile);

        if (!closedCastles.isEmpty()) {
            this.closedCastles.addAll(closedCastles);
            eventCallbacks.forEach(callback -> callback.onRegionClosed(TileContent.CASTLE, closedCastles));
        }
        if (!closedStreets.isEmpty()) {
            eventCallbacks.forEach(callback -> callback.onRegionClosed(TileContent.STREET, closedStreets));
        }
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

    public List<AdjacentClosedCastlesToGrasComponentComputer.ClosedCastlesForGrasComponent> closedCastlesAdjacentToGraphComponent() {
        return new AdjacentClosedCastlesToGrasComponentComputer(
                filterGraphsForContent(TileContent.GRAS),
                closedCastles,
                tileLookup
        ).compute();
    }

    public void registerEventCallback(BoardGraphEventCallback callback) {
        this.eventCallbacks.add(callback);
    }

    public Optional<Graph> getGraphThatContainsNode(int x, int y, int row, int column) {
        var node = new Graph.Node(x, y, row, column);
        return graphs
                .stream()
                .filter(graph -> graph.nodes().contains(node))
                .findFirst();
    }

    public Set<Set<Graph.Node>> closedCastles(Tile tile) {
        return closedRegions(tile, filterGraphsForContent(TileContent.CASTLE));
    }

    public Set<Set<Graph.Node>> closedStreets(Tile tile) {
        // TODO: add cycle detection for streets
        return closedRegions(tile, filterGraphsForContent(TileContent.STREET));
    }

    public Set<Tile> distinctTilesInNodeSet(Set<Graph.Node> nodes) {
        return nodes.stream()
                .map(node -> tileLookup.resolve(node.getX(), node.getY()))
                .collect(Collectors.toSet());
    }

    public int getStreetLength(Tile tile) {
        var closedStreets = closedStreets(tile);

        return closedStreets.isEmpty()
                ? UNCLOSED_STREET
                : distinctTilesInNodeSet(closedStreets.iterator().next()).size();
    }

    public void validateFigurePlacement(Figure figure, Collection<Figure> existingFigures) throws RegionOccupiedException {
        var figurePosition = figure.getPosition();
        var graphContainingFigure = getGraphThatContainsNode(figurePosition.getX(), figurePosition.getY(), figurePosition.getRow(), figurePosition.getColumn());
        if (graphContainingFigure.isEmpty()) {
            throw new IllegalArgumentException(String.format("Tile region type needs to be one of [%s]", VALID_FIGURE_REGIONS.stream().map(TileContent::name).collect(Collectors.joining(", "))));
        }

        validateUniqueFigurePositionInWcc(graphContainingFigure.get(), figure, existingFigures);
    }

    public Graph filterGraphsForContent(TileContent tileContent) {
        return graphs.stream()
                .filter(g -> g.tileContent() == tileContent)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No graph was found for TileContent %s", TileContent.STREET)));
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

    private Set<Set<Graph.Node>> closedRegions(Tile tile, Graph castleGraph) {
        var closedCastlesTracker = new ClosedRegionsTracker(castleGraph, tileLookup);
        var closedCastleNodes = closedCastlesTracker.closedRegionNodes(tile);

        var graphBfs = new GraphBfs(castleGraph);
        Set<Set<Graph.Node>> closedCastles = new HashSet<>();
        for (Graph.Node closedCastleNode : closedCastleNodes) {
            Set<Graph.Node> closedCastleComponent = new HashSet<>();
            closedCastles.add(closedCastleComponent);
            graphBfs.compute(closedCastleNode, (node, neighbors) -> closedCastleComponent.add(node));
        }
        return closedCastles;
    }

    @FunctionalInterface
    public interface TileLookup {
        Tile resolve(int x, int y);
    }

    @FunctionalInterface
    public interface BoardGraphEventCallback {
        void onRegionClosed(TileContent regionType, Set<Set<Graph.Node>> closedRegions);
    }

    @Value
    static class Position {
        int x;
        int y;
    }
}
