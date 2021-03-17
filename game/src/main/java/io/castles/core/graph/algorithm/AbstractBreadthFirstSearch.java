package io.castles.core.graph.algorithm;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class AbstractBreadthFirstSearch<T> {

    abstract Collection<T> getNeighbors(T element);

    public void compute(T initialElement, BiConsumer<T, Collection<T>> consumer) {
        Set<T> seen = new HashSet<>();
        Queue<T> queue = new LinkedBlockingQueue<>();

        queue.offer(initialElement);
        while(!queue.isEmpty()) {
            T element = queue.poll();

            if (!seen.contains(element)) {
                Collection<T> neighbors = getNeighbors(element);
                Collection<T> unseenNeighbors = neighbors.stream()
                        .filter(neighbor -> !seen.contains(neighbor))
                        .collect(Collectors.toList());
                consumer.accept(element, unseenNeighbors);
                unseenNeighbors.forEach(queue::offer);
            } else {
                consumer.accept(element, List.of());
            }

            seen.add(element);
        }
    }
}
