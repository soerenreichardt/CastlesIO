package io.castles.game.events;

import io.castles.game.GameLobby;

public interface GlobalEventConsumer {
    void onLobbyCreated(GameLobby gameLobby);
}
