package io.castles.game.events;

import io.castles.game.IdentifiableObject;
import io.castles.game.Lifecycle;

import java.util.UUID;

public abstract class StatefulObject extends IdentifiableObject implements EventProducer<GameEvent>, Lifecycle {

    protected final EventHandler eventHandler;
    private boolean initialized;

    public StatefulObject(UUID id, EventHandler eventHandler) {
        super(id);
        this.eventHandler = eventHandler;
        this.initialized = false;
    }

    @Override
    public void triggerGlobalEvent(GameEvent event, Object... objects) {
        this.eventHandler.triggerGlobalEvent(event, objects);
    }

    @Override
    public void triggerLocalEvent(UUID id, GameEvent event, Object... objects) {
        if (!initialized) {
            throw new IllegalStateException("Cannot trigger event on uninitialized class. Please call `StatefulObject#initialize");
        }
        this.eventHandler.triggerLocalEvent(getId(), event, objects);
    }

    public EventHandler eventHandler() {
        return this.eventHandler;
    }

    @Override
    public void initialize() {
        this.initialized = true;
        init();
    }

    protected abstract void init();
}
