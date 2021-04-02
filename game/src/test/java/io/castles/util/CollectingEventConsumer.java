package io.castles.util;

import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.events.GameEvent;
import io.castles.game.events.GameEventConsumer;
import io.castles.game.events.GlobalEventConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void collect(String event, Object data) {
        events.computeIfAbsent(event, __ -> new ArrayList<>()).add(data.toString());
    }

    public void reset() {
        events.clear();
    }
}
