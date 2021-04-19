package io.castles.core.graph.algorithm;

import io.castles.core.graph.Graph;

import java.util.Collection;
import java.util.Set;

public class GraphBfs extends AbstractBreadthFirstSearch<Graph.Node> {

    private final Graph graph;

    public GraphBfs(Graph graph) {
        this.graph = graph;
    }

    @Override
    Collection<Graph.Node> getNeighbors(Graph.Node node) {
        return graph.relationships().getOrDefault(node, Set.of());
    }
}
