package io.castles.game.events;

@FunctionalInterface
public interface EventProducer {
    void triggerEvent(Event event, Object... objects);
}
