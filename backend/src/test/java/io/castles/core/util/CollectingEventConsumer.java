package io.castles.core.util;

import io.castles.core.events.ServerEvent;
import io.castles.core.events.ServerEventConsumer;
import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.events.GameEvent;
import io.castles.game.events.GameEventConsumer;

import java.util.*;

public class CollectingEventConsumer implements ServerEventConsumer, GameEventConsumer {

    Map<String, List<String>> events = new HashMap<>();

    public Map<String, List<String>> events() {
        return events;
    }

    @Override
    public void onPlayerReconnectAttempt(Player player) {
        collect(ServerEvent.PLAYER_RECONNECT_ATTEMPT.name(), player.getId());
    }

    @Override
    public void onPlayerTimeout(Player player) {
        collect(ServerEvent.PLAYER_TIMEOUT.name(), player.getId());
    }

    @Override
    public void onPlayerReconnected(Player player) {
        collect(ServerEvent.PLAYER_RECONNECTED.name(), player.getId());
    }

    @Override
    public void onPlayerDisconnected(Player player) {
        collect(ServerEvent.PLAYER_DISCONNECTED.name(), player.getId());
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
