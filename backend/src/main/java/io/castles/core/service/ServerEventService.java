package io.castles.core.service;

import io.castles.core.events.ServerEvent;
import io.castles.core.events.ServerEventConsumer;
import io.castles.game.Player;
import io.castles.game.Server;
import io.castles.game.events.GameEventConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerEventService {

    Server server;
    Map<UUID, List<ServerEventConsumer>> serverEventConsumers;
    List<EventConsumerSupplier<?>> eventConsumerSuppliers;

    public ServerEventService(Server server) {
        this.server = server;
        this.serverEventConsumers = new ConcurrentHashMap<>();
        this.eventConsumerSuppliers = new ArrayList<>();
    }

    public <T> void registerEventConsumerSupplier(EventConsumerSupplier<T> eventConsumerSupplier) {
        this.eventConsumerSuppliers.add(eventConsumerSupplier);
    }

    public void initializeEventConsumersWithId(UUID id) {
        eventConsumerSuppliers
                .stream()
                .map(supplier -> supplier.get(id))
                .forEach(eventConsumer -> {
                    if (eventConsumer instanceof ServerEventConsumer) {
                        serverEventConsumers.computeIfAbsent(id, __ -> new ArrayList<>()).add((ServerEventConsumer) eventConsumer);
                    }
                    if (eventConsumer instanceof GameEventConsumer) {
                        server.eventHandler().registerEventConsumerLate((GameEventConsumer) eventConsumer);
                    }
                });
    }

    public void triggerEvent(UUID id, ServerEvent serverEvent, Object... objects) {
        var serverEventConsumerList = serverEventConsumers.get(id);
        switch (serverEvent) {
            case PLAYER_RECONNECTED ->  serverEventConsumerList.forEach(c -> c.onPlayerReconnected((Player) objects[0]));
            case PLAYER_DISCONNECTED -> serverEventConsumerList.forEach(c -> c.onPlayerDisconnected((Player) objects[0]));
        }
    }

    @FunctionalInterface
    public interface EventConsumerSupplier<T> {
        T get(UUID id);
    }
}
