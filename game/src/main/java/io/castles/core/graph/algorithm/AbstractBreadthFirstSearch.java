package io.castles.core.graph.algorithm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public abstract class AbstractBreadthFirstSearch<T> {

    abstract Collection<T> getNeighbors(T element);

    public void compute(T initialElement, BfsVisitor<T> consumer) {
        Set<T> seen = new HashSet<>();
        Queue<T> queue = new LinkedBlockingQueue<>();

        queue.offer(initialElement);
        while(!queue.isEmpty()) {
            T element = queue.poll();

            if (!seen.contains(element)) {
                Collection<T> neighbors = getNeighbors(element);
                // Our graph implementation is an undirected graph
                // so we need to filter already seen nodes
                Collection<T> unseenNeighbors = neighbors.stream()
                        .filter(neighbor -> !seen.contains(neighbor))
                        .collect(Collectors.toList());
                if (!consumer.visit(element, unseenNeighbors)) {
                    return;
                }
                unseenNeighbors.forEach(queue::offer);
            }

            seen.add(element);
        }
    }

    public interface BfsVisitor<T> {
        boolean visit(T element, Collection<T> neighbors);
    }
}
