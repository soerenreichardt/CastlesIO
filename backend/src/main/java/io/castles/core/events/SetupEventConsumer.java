package io.castles.core.events;

import io.castles.core.service.ServerEventService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.GameLobby;
import io.castles.game.events.GlobalEventConsumer;

public class SetupEventConsumer implements GlobalEventConsumer {

    private final ServerEventService serverEventService;
    private final SseEmitterService emitterService;

    public SetupEventConsumer(ServerEventService serverEventService, SseEmitterService emitterService) {
        this.serverEventService = serverEventService;
        this.emitterService = emitterService;
    }

    @Override
    public void onLobbyCreated(GameLobby gameLobby) {
        emitterService.createLobbyEmitter(gameLobby);
        serverEventService.initializeEventConsumersWithId(gameLobby.getId());
    }
}
