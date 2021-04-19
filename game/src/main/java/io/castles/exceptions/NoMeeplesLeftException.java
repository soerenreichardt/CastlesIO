package io.castles.exceptions;

import io.castles.game.Player;

public class NoMeeplesLeftException extends Exception {
    public NoMeeplesLeftException(Player player) {
        super("No meeples left for player " + player.toString());
    }
}
