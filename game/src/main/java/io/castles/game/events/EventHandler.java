package io.castles.game.events;

import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventHandler implements EventProducer<GameEvent> {

    private final List<GlobalEventConsumer> globalEventCallbacks;
    private final Map<UUID, List<GameEventConsumer>> localEventCallbacks;

    public EventHandler() {
        this.globalEventCallbacks = new ArrayList<>();
        this.localEventCallbacks = new ConcurrentHashMap<>();
    }

    public void registerGlobalEventConsumer(GlobalEventConsumer callback) {
        this.globalEventCallbacks.add(callback);
    }

    public void registerLocalEventConsumer(UUID id, GameEventConsumer callback) {
        this.localEventCallbacks.computeIfAbsent(id, __ -> new ArrayList<>()).add(callback);
    }

    @Override
    public void triggerGlobalEvent(GameEvent event, Object... objects) {
        switch (event) {
            case LOBBY_CREATED -> globalEventCallbacks.forEach(consumer -> consumer.onLobbyCreated((GameLobby) objects[0]));
        }
    }

    @Override
    public void triggerLocalEvent(UUID id, GameEvent event, Object... objects) {
        var eventConsumers = localEventCallbacks.get(id);
        if (eventConsumers != null) {
            switch (event) {
                case PLAYER_ADDED -> eventConsumers.forEach(consumer -> consumer.onPlayerAdded((Player) objects[0]));
                case PLAYER_REMOVED -> eventConsumers.forEach(consumer -> consumer.onPlayerRemoved((Player) objects[0]));
                case SETTINGS_CHANGED -> eventConsumers.forEach(consumer -> consumer.onSettingsChanged((GameLobbySettings) objects[0]));
            }
        }
    }

    public void reset() {
        globalEventCallbacks.clear();
        localEventCallbacks.clear();
    }
}
