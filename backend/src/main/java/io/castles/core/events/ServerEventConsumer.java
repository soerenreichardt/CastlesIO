package io.castles.core.events;

import io.castles.game.events.StatefulObject;

import java.util.UUID;

public interface ServerEventConsumer extends StatefulObject.EventConsumer {
    void onPlayerReconnected(UUID playerId);
}
