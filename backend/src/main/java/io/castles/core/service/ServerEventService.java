package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.core.events.ServerEventConsumer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerEventService {

    Map<UUID, ServerEventConsumer> serverEventConsumers = new ConcurrentHashMap<>();

    public void registerEventConsumer(UUID lobbyId, ServerEventConsumer eventConsumer) {
        serverEventConsumers.put(lobbyId, eventConsumer);
    }

    public void triggerEvent(UUID lobbyId, ServerEvent serverEvent, Object... objects) {
        var serverEventConsumer = serverEventConsumers.get(lobbyId);
        switch (serverEvent) {
            case PLAYER_RECONNECTED -> serverEventConsumer.onPlayerReconnected((UUID) objects[0]);
        }
    }
}
