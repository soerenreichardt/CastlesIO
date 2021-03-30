package io.castles.core.events;

import io.castles.core.service.ServerEventService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.GameLobby;
import io.castles.game.events.GameEventConsumer;

public class SetupEventConsumer extends GameEventConsumer.Adapter {

    private final ServerEventService serverEventService;
    private final SseEmitterService emitterService;

    public SetupEventConsumer(ServerEventService serverEventService, SseEmitterService emitterService) {
        this.serverEventService = serverEventService;
        this.emitterService = emitterService;
    }

    @Override
    public void onLobbyCreated(GameLobby gameLobby) {
        serverEventService.initializeEventConsumersWithId(gameLobby.getId());
        emitterService.createLobbyEmitter(gameLobby);
    }
}
