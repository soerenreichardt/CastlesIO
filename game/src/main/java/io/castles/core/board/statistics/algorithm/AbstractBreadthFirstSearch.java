package io.castles.core.board.statistics.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public abstract class AbstractBreadthFirstSearch<T> {

    abstract List<T> getNeighbors(T element);

    public void compute(T initialElement, Consumer<T> consumer) {
        Set<T> seen = new HashSet<>();
        Queue<T> queue = new LinkedBlockingQueue<>();

        queue.offer(initialElement);
        while(!queue.isEmpty()) {
            T element = queue.poll();

            if (!seen.contains(element)) {
                getNeighbors(element).forEach(queue::offer);
            }

            consumer.accept(element);
            seen.add(element);
        }
    }
}
