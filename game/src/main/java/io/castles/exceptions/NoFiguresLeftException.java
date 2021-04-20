package io.castles.exceptions;

import io.castles.game.Player;

public class NoFiguresLeftException extends Exception {
    public NoFiguresLeftException(Player player) {
        super("No figures left for player " + player.toString());
    }
}
