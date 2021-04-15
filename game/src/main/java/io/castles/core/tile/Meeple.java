package io.castles.core.tile;

import io.castles.core.graph.Graph;
import io.castles.game.Player;

public class Meeple {

    private final Graph.Node position;
    private final Player owner;

    public Meeple(Graph.Node position, Player owner) {
        this.position = position;
        this.owner = owner;
    }
}
