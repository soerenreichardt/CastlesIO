package io.castles.game.events;

import java.util.UUID;

public interface EventProducer<T> {
    void triggerGlobalEvent(T event, Object... objects);
    void triggerLocalEvent(UUID id, T event, Object... objects);
}
