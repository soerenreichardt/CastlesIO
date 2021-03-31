package io.castles.core.events;

import io.castles.game.Player;

public interface ServerEventConsumer {
    void onPlayerReconnected(Player player);
    void onPlayerDisconnected(Player player);
}
