package io.castles.game;

import java.util.Collection;
import java.util.UUID;

public interface PlayerContainer {
    Player getPlayerById(UUID playerId);
    boolean containsPlayer(UUID playerId);
    Collection<Player> getPlayers();
}
