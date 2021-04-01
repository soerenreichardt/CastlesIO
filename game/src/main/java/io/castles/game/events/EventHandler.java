package io.castles.game.events;

import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;

import java.util.ArrayList;
import java.util.List;

public class EventHandler implements EventProducer<GameEvent> {

    private final List<GameEventConsumer> eventCallbacks;
    private final List<GameEventConsumer> lateRegisteredCallbacks;

    public EventHandler() {
        this.eventCallbacks = new ArrayList<>();
        this.lateRegisteredCallbacks = new ArrayList<>();
    }

    public void registerEventConsumer(GameEventConsumer callback) {
        this.eventCallbacks.add(callback);
    }

    public void registerEventConsumerLate(GameEventConsumer callback) {
        this.lateRegisteredCallbacks.add(callback);
    }

    @Override
    public void triggerEvent(GameEvent event, Object... objects) {
        switch (event) {
            case PLAYER_ADDED -> eventCallbacks.forEach(consumer -> consumer.onPlayerAdded((Player) objects[0]));
            case PLAYER_REMOVED -> eventCallbacks.forEach(consumer -> consumer.onPlayerRemoved((Player) objects[0]));
            case SETTINGS_CHANGED -> eventCallbacks.forEach(consumer -> consumer.onSettingsChanged((GameLobbySettings) objects[0]));
            case LOBBY_CREATED -> eventCallbacks.forEach(consumer -> consumer.onLobbyCreated((GameLobby) objects[0]));
        }
        mergeLateRegisteredCallbacks();
    }

    private void mergeLateRegisteredCallbacks() {
        if (!lateRegisteredCallbacks.isEmpty()) {
            eventCallbacks.addAll(lateRegisteredCallbacks);
        }
    }
}
