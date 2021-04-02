package io.castles.core.events;

import io.castles.game.Player;

public interface ServerEventConsumer {
    void onPlayerReconnectAttempt(Player player);
    void onPlayerReconnected(Player player);
    void onPlayerDisconnected(Player player);
    void onPlayerTimeout(Player player);
}
