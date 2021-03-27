package io.castles.game.events;

import io.castles.game.GameLobbySettings;
import io.castles.game.Player;

public interface EventConsumer {

    void onPlayerAdded(Player player);

    void onPlayerRemoved(Player player);

    void onSettingsChanged(GameLobbySettings gameLobbySettings);
}
