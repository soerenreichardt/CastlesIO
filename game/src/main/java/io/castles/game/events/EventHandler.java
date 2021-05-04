package io.castles.game.events;

import io.castles.core.tile.Tile;
import io.castles.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventHandler implements EventProducer<GameEvent> {

    private final List<GlobalEventConsumer> globalEventCallbacks;
    private final Map<UUID, List<GameEventConsumer>> localEventCallbacks;

    public EventHandler() {
        this(new ArrayList<>(), new ConcurrentHashMap<>());
    }

    protected EventHandler(List<GlobalEventConsumer> globalEventCallbacks, Map<UUID, List<GameEventConsumer>> localEventCallbacks) {
        this.globalEventCallbacks = globalEventCallbacks;
        this.localEventCallbacks = localEventCallbacks;
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
                case GAME_STARTED -> eventConsumers.forEach(consumer -> consumer.onGameStarted((Game) objects[0]));
                case PHASE_SWITCHED -> eventConsumers.forEach(consumer -> consumer.onPhaseSwitched((GameState) objects[0], (GameState) objects[1]));
                case ACTIVE_PLAYER_SWITCHED -> eventConsumers.forEach(consumer -> consumer.onActivePlayerSwitched((Player) objects[0]));
                case TILE_PLACED -> eventConsumers.forEach(consumer -> consumer.onTilePlaced((Tile) objects[0], (int) objects[1], (int) objects[2], (int) objects[3]));
                case FIGURE_PLACED -> eventConsumers.forEach(consumer -> consumer.onFigurePlaced((Player) objects[0], (Tile) objects[1], (int) objects[2], (int) objects[3]));
                case GAME_END -> eventConsumers.forEach(GameEventConsumer::onGameEnd);
            }
        }
    }

    public LocalEventHandler toLocalEventHandlerCopy(UUID id) {
        return new LocalEventHandler(id, localEventCallbacks);
    }
}
