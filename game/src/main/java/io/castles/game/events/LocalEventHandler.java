package io.castles.game.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LocalEventHandler extends EventHandler {

    private final UUID id;

    public LocalEventHandler(UUID id, Map<UUID, List<GameEventConsumer>> localEventCallbacks) {
        super(new ArrayList<>(), localEventCallbacks);
        this.id = id;
    }

    @Override
    public void triggerGlobalEvent(GameEvent event, Object... objects) {
        throw new UnsupportedOperationException("Global event");
    }

    public void triggerLocalEvent(GameEvent event, Object... objects) {
        triggerLocalEvent(id, event, objects);
    }
}
