package io.castles.core.graph.algorithm;

import io.castles.core.graph.Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Wcc {

    private final Graph graph;
    private final Map<Graph.Node, Graph.Node> components;

    public Wcc(Graph graph) {
        this.graph = graph;
        this.components = initialComponents();
    }

    public void compute() {
        graph.forEachRelationship(this::union);
    }

    public void union(Graph.Node lhs, Graph.Node rhs) {
        Graph.Node node1 = find(lhs);
        Graph.Node node2 = find(rhs);
        if (node1.equals(node2)) {
            return;
        }

        if (node1.hashCode() < node2.hashCode()) {
            var tmp = node2;
            node2 = node1;
            node1 = tmp;
        }

        components.put(node1, node2);
    }

    public boolean sameComponent(Graph.Node lhs, Graph.Node rhs) {
        while (true) {
            Graph.Node node1 = find(lhs);
            Graph.Node node2 = find(rhs);
            if (node1.equals(node2)) {
                return true;
            }
            if (components.get(node1).equals(node1)) {
                return false;
            }
        }
    }

    public Map<Integer, Set<Graph.Node>> getNodesByComponent() {
        Map<Integer, Set<Graph.Node>> nodesByComponent = new HashMap<>();
        Map<Graph.Node, Integer> componentIdMapping = new HashMap<>();
        AtomicInteger componentId = new AtomicInteger(0);

        components.forEach((node, parent) -> {
            var component = find(parent);
            componentIdMapping.computeIfAbsent(component, __ -> componentId.getAndIncrement());
            nodesByComponent.computeIfAbsent(componentIdMapping.get(component), __ -> new HashSet<>()).add(node);
        });
        return nodesByComponent;
    }

    private Graph.Node find(Graph.Node node) {
        Graph.Node component = node;
        Graph.Node parent;
        while(!component.equals(parent = components.get(component))) {
            component = parent;
        }
        return component;
    }

    private Map<Graph.Node, Graph.Node> initialComponents() {
        var components = new HashMap<Graph.Node, Graph.Node>();
        graph.nodes().forEach(node -> components.put(node, node));
        return components;
    }

}
