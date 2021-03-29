package io.castles.game.events;

import io.castles.game.IdentifiableObject;

import java.util.UUID;

public abstract class StatefulObject extends IdentifiableObject implements EventProducer<GameEvent> {

    protected final EventHandler eventHandler;

    public StatefulObject(UUID id, EventHandler eventHandler) {
        super(id);
        this.eventHandler = eventHandler;
    }

    @Override
    public void triggerEvent(GameEvent event, Object... objects) {
        this.eventHandler.triggerEvent(event, objects);
    }

    public EventHandler eventHandler() {
        return this.eventHandler;
    }

}
