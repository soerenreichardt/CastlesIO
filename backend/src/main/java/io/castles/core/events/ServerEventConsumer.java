package io.castles.core.events;

import io.castles.game.events.EventConsumer;

import java.util.UUID;

public interface ServerEventConsumer extends EventConsumer {
    void onPlayerReconnected(UUID playerId);
}
