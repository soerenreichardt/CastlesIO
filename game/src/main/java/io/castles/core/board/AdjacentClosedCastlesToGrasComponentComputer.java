package io.castles.core.board;

import io.castles.core.graph.Graph;
import io.castles.core.graph.algorithm.Wcc;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.TileContent;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AdjacentClosedCastlesToGrasComponentComputer {


    private final Graph graph;
    private final Set<Set<Graph.Node>> closedCastles;
    private final BoardGraph.TileLookup tileLookup;

    public AdjacentClosedCastlesToGrasComponentComputer(Graph graph, Set<Set<Graph.Node>> closedCastles, BoardGraph.TileLookup tileLookup) {
        this.graph = graph;
        this.closedCastles = closedCastles;
        this.tileLookup = tileLookup;
    }

    public List<ClosedCastlesForGrasComponent> compute() {
        var closedCastlesPerGrasComponent = new ArrayList<ClosedCastlesForGrasComponent>();
        var grasComponents = computeGrasWcc();
        for (Set<Graph.Node> grasComponent : grasComponents) {
            var adjacentClosedCastles = closedCastlesAdjacentToGrasRegion(grasComponent);
            closedCastlesPerGrasComponent.add(new ClosedCastlesForGrasComponent(adjacentClosedCastles, grasComponent));
        }
        return closedCastlesPerGrasComponent;
    }

    @Value
    @EqualsAndHashCode
    public static class ClosedCastlesForGrasComponent {
        int closedCastles;
        Set<Graph.Node> grasComponent;
    }

    public Collection<Set<Graph.Node>> computeGrasWcc() {
        var wcc = new Wcc(graph);
        wcc.compute();
        return wcc.getNodesByComponent().values();
    }

    private int closedCastlesAdjacentToGrasRegion(Set<Graph.Node> grasRegion) {
        Set<Set<Graph.Node>> seenClosedCastles = new HashSet<>();
        var adjacentClosedCastles = new AtomicInteger(0);
        grasRegion.forEach(grasNode -> {
            // all neighbors are also gras -> no need to check for closed castles
            var allNeighborsAreGras = graph.relationships().get(grasNode).size() == 4;
            if (allNeighborsAreGras || !nodeIsOnEdgeOfTile(grasNode)) {
                return;
            }

            var adjacentCastleNodes = adjacentCastleNodes(grasNode);
            closedCastles.stream()
                    .filter(closedCastle -> !seenClosedCastles.contains(closedCastle))
                    .forEach(closedCastle -> {
                        for (Graph.Node adjacentCastleNode : adjacentCastleNodes) {
                            if (closedCastle.contains(adjacentCastleNode)) {
                                adjacentClosedCastles.incrementAndGet();
                                seenClosedCastles.add(closedCastle);
                                return;
                            }
                        }
                    });
        });
        return adjacentClosedCastles.get();
    }

    private boolean nodeIsOnEdgeOfTile(Graph.Node node) {
        var tile = tileLookup.resolve(node.getX(), node.getY());
        var contentMatrix = tile.<MatrixTileLayout>getTileLayout().getContent();
        var columns = contentMatrix.getColumns();
        var rows = contentMatrix.getRows();

        var row = node.getRow();
        var column = node.getColumn();

        if (row == 0 || column == 0 || row == rows - 1 || column == columns - 1) {
            return true;
        }
        return false;
    }

    private Set<Graph.Node> adjacentCastleNodes(Graph.Node node) {
        var tile = tileLookup.resolve(node.getX(), node.getY());
        var contentMatrix = tile.<MatrixTileLayout>getTileLayout().getContent();

        var row = node.getRow();
        var column = node.getColumn();

        Set<Graph.Node> adjacentCastleNodes = new HashSet<>();
        if (row > 0 && contentMatrix.get(row - 1, column).matches(TileContent.CASTLE)) adjacentCastleNodes.add(new Graph.Node(node.getX(), node.getY(), row - 1, column));
        if (row < contentMatrix.getRows() - 1 && contentMatrix.get(row + 1, column).matches(TileContent.CASTLE)) adjacentCastleNodes.add(new Graph.Node(node.getX(), node.getY(), row + 1, column));
        if (column > 0 && contentMatrix.get(row , column - 1).matches(TileContent.CASTLE)) adjacentCastleNodes.add(new Graph.Node(node.getX(), node.getY(), row, column - 1));
        if (column < contentMatrix.getColumns() - 1 && contentMatrix.get(row , column + 1).matches(TileContent.CASTLE)) adjacentCastleNodes.add(new Graph.Node(node.getX(), node.getY(), row, column + 1));
        return adjacentCastleNodes;
    }
}
