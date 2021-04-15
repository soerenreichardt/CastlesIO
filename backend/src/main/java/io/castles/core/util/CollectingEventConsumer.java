package io.castles.core.util;

import io.castles.core.events.ServerEvent;
import io.castles.core.events.ServerEventConsumer;
import io.castles.game.Player;

public class CollectingEventConsumer extends io.castles.util.CollectingEventConsumer implements ServerEventConsumer {

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
}
