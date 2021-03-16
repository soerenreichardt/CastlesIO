package io.castles.core.graph.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;

public abstract class AbstractBreadthFirstSearch<T> {

    abstract List<T> getNeighbors(T element);

    public void compute(T initialElement, BiConsumer<T, List<T>> consumer) {
        Set<T> seen = new HashSet<>();
        Queue<T> queue = new LinkedBlockingQueue<>();

        queue.offer(initialElement);
        while(!queue.isEmpty()) {
            T element = queue.poll();

            if (!seen.contains(element)) {
                List<T> neighbors = getNeighbors(element);
                consumer.accept(element, neighbors);
                neighbors.forEach(queue::offer);
            } else {
                consumer.accept(element, List.of());
            }

            seen.add(element);
        }
    }
}
