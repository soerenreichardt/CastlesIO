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
    public void onPlayerReconnected(UUID playerId) {
        events.computeIfAbsent(ServerEvent.PLAYER_RECONNECTED.name(), __ -> new ArrayList<>()).add(playerId.toString());
    }

    @Override
    public void onPlayerAdded(Player player) {
        events.computeIfAbsent(GameEvent.PLAYER_ADDED.name(), __ -> new ArrayList<>()).add(player.toString());
    }

    @Override
    public void onPlayerRemoved(Player player) {
        events.computeIfAbsent(GameEvent.PLAYER_REMOVED.name(), __ -> new ArrayList<>()).add(player.toString());
    }

    @Override
    public void onSettingsChanged(GameLobbySettings gameLobbySettings) {
        events.computeIfAbsent(GameEvent.SETTINGS_CHANGED.name(), __ -> new ArrayList<>()).add(gameLobbySettings.toString());
    }

    @Override
    public void onLobbyCreated(GameLobby gameLobby) {
        events.computeIfAbsent(GameEvent.LOBBY_CREATED.name(), __ -> new ArrayList<>()).add(gameLobby.toString());
    }

    public void reset() {
        events.clear();
    }
}
