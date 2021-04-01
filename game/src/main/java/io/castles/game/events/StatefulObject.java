package io.castles.game.events;

import io.castles.game.IdentifiableObject;

import java.util.UUID;

public abstract class StatefulObject extends IdentifiableObject implements EventProducer<GameEvent> {

    protected final EventHandler eventHandler;
    private boolean initialized;

    public StatefulObject(UUID id, EventHandler eventHandler) {
        super(id);
        this.eventHandler = eventHandler;
        this.initialized = false;
    }

    @Override
    public void triggerEvent(GameEvent event, Object... objects) {
        if (!initialized) {
            throw new IllegalStateException("Cannot trigger event on uninitialized class. Please call `StatefulObject#initialize");
        }
        this.eventHandler.triggerEvent(event, objects);
    }

    public EventHandler eventHandler() {
        return this.eventHandler;
    }

    public void initialize() {
        this.initialized = true;
        triggerEvent(GameEvent.LOBBY_CREATED, this);
        init();
    }

    protected abstract void init();
}
