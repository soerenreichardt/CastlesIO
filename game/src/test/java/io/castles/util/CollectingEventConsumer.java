package io.castles.util;

import io.castles.game.*;
import io.castles.game.events.GameEvent;
import io.castles.game.events.GameEventConsumer;
import io.castles.game.events.GlobalEventConsumer;

import java.util.*;
import java.util.stream.Collectors;

public class CollectingEventConsumer implements GameEventConsumer, GlobalEventConsumer {

    Map<String, List<String>> events = new HashMap<>();

    public Map<String, List<String>> events() {
        return events;
    }

    @Override
    public void onPlayerAdded(Player player) {
        collect(GameEvent.PLAYER_ADDED.name(), player);
    }

    @Override
    public void onPlayerRemoved(Player player) {
        collect(GameEvent.PLAYER_REMOVED.name(), player);
    }

    @Override
    public void onSettingsChanged(GameLobbySettings gameLobbySettings) {
        collect(GameEvent.SETTINGS_CHANGED.name(), gameLobbySettings);
    }

    @Override
    public void onLobbyCreated(GameLobby gameLobby) {
        collect(GameEvent.LOBBY_CREATED.name(), gameLobby);
    }

    @Override
    public void onGameStarted(Game game) {
        collect(GameEvent.GAME_STARTED.name(), game);
    }

    @Override
    public void onActivePlayerSwitched(Player activePlayer) {
        collect(GameEvent.ACTIVE_PLAYER_SWITCHED.name(), activePlayer);
    }

    @Override
    public void onPhaseSwitched(GameState from, GameState to) {
        collect(GameEvent.PHASE_SWITCHED.name(), from, to);
    }

    public void collect(String event, Object... data) {
        var objectsList = Arrays.stream(data).map(Object::toString).collect(Collectors.toList());
        events.computeIfAbsent(event, __ -> new ArrayList<>()).add(String.join(", ", objectsList));
    }

    public void reset() {
        events.clear();
    }
}
