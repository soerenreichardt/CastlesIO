package io.castles.game.events;

import io.castles.game.GameLobbySettings;
import io.castles.game.Player;

public interface GameEventConsumer {

    void onPlayerAdded(Player player);

    void onPlayerRemoved(Player player);

    void onSettingsChanged(GameLobbySettings gameLobbySettings);

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
    }
}
