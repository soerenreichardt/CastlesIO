package io.castles.game.events;

import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;

public interface GameEventConsumer {

    void onPlayerAdded(Player player);

    void onPlayerRemoved(Player player);

    void onSettingsChanged(GameLobbySettings gameLobbySettings);

    void onLobbyCreated(GameLobby gameLobby);

    class Adapter implements GameEventConsumer {
        @Override
        public void onPlayerAdded(Player player) {

        }

        @Override
        public void onPlayerRemoved(Player player) {

        }

        @Override
        public void onSettingsChanged(GameLobbySettings gameLobbySettings) {

        }

        @Override
        public void onLobbyCreated(GameLobby gameLobby) {

        }
    }
}
