package io.castles.core.events;

import io.castles.core.service.ClockService;
import io.castles.core.service.PlayerEmitters;
import io.castles.game.Player;

import java.util.*;

public class ConnectionHandler {

    public static final long DISCONNECT_TIMEOUT = 60_000L;

    private final PlayerEmitters playerEmitters;
    private final ClockService clockService;
    private final Map<Player, Long> disconnectedPlayers;

    public ConnectionHandler(PlayerEmitters playerEmitters, ClockService clockService) {
        this.playerEmitters = playerEmitters;
        this.clockService = clockService;
        this.disconnectedPlayers = new HashMap<>();
    }

    public boolean tryReconnectPlayer(Player player) {
        if (disconnectedPlayers.containsKey(player)) {
            playerEmitters.create(player.getId());
            return true;
        }
        return false;
    }

    public void checkDisconnectionTimeout() {
        var currentTime = clockService.instance().millis();
        List<Player> timeoutPlayers = new ArrayList<>();
        disconnectedPlayers.forEach((player, disconnectionTime) -> {
            if (currentTime - disconnectionTime > DISCONNECT_TIMEOUT) {
                timeoutPlayers.add(player);
            }
        });
        timeoutPlayers.forEach(disconnectedPlayers::remove);
    }

    public void playerDisconnected(Player player) {
        disconnectedPlayers.put(player, clockService.instance().millis());
        playerEmitters.remove(player.getId());
    }

    public Set<Player> disconnectedPlayers() {
        return disconnectedPlayers.keySet();
    }
}
