package io.castles.game.events;

@FunctionalInterface
public interface EventProducer<T> {
    void triggerEvent(T event, Object... objects);
}
