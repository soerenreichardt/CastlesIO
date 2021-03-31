package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.core.events.ServerEventConsumer;
import io.castles.game.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerEventService {

    Map<UUID, ServerEventConsumer> serverEventConsumers;
    List<EventConsumerSupplier> eventConsumerSuppliers;

    public ServerEventService() {
        this.serverEventConsumers = new ConcurrentHashMap<>();
        this.eventConsumerSuppliers = new ArrayList<>();
    }

    public void registerEventConsumerSupplier(EventConsumerSupplier eventConsumerSupplier) {
        this.eventConsumerSuppliers.add(eventConsumerSupplier);
    }

    public void initializeEventConsumersWithId(UUID id) {
        eventConsumerSuppliers
                .stream()
                .map(supplier -> supplier.get(id))
                .forEach(eventConsumer -> serverEventConsumers.put(id, eventConsumer));
    }

    public void triggerEvent(UUID id, ServerEvent serverEvent, Object... objects) {
        var serverEventConsumer = serverEventConsumers.get(id);
        switch (serverEvent) {
            case PLAYER_RECONNECTED -> serverEventConsumer.onPlayerReconnected((Player) objects[0]);
            case PLAYER_DISCONNECTED -> serverEventConsumer.onPlayerDisconnected((Player) objects[0]);
        }
    }

    @FunctionalInterface
    public interface EventConsumerSupplier {
        ServerEventConsumer get(UUID id);
    }
}
