package io.castles.core.events;

import java.util.UUID;

public interface ServerEventConsumer {
    void onPlayerReconnected(UUID playerId);
}
