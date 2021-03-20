package io.castles.game.events;

import io.castles.game.GameLobbySettings;
import io.castles.game.IdentifiableObject;
import io.castles.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class StatefulObject extends IdentifiableObject {

    private final List<EventConsumer> eventCallbacks;

    protected StatefulObject() {
        this(UUID.randomUUID());
    }

    protected StatefulObject(UUID id) {
        super(id);
        this.eventCallbacks = new ArrayList<>();
    }

    public abstract void initializeWith(EventConsumer eventConsumer);

    public void registerCallback(EventConsumer callback) {
        this.eventCallbacks.add(callback);
    }

    public void triggerEvent(Event event, Object... objects) {
        switch (event) {
            case PLAYER_ADDED -> eventCallbacks.forEach(consumer -> consumer.onPlayerAdded((Player) objects[0]));
            case PLAYER_REMOVED -> eventCallbacks.forEach(consumer -> consumer.onPlayerRemoved((Player) objects[0]));
            case SETTINGS_CHANGED -> eventCallbacks.forEach(consumer -> consumer.onSettingsChanged((GameLobbySettings) objects[0]));
        }
    }

    public interface EventConsumer {

        void onPlayerAdded(Player player);

        void onPlayerRemoved(Player player);

        void onSettingsChanged(GameLobbySettings gameLobbySettings);
    }
}
