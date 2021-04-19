package io.castles.core.tile;

import io.castles.core.graph.Graph;
import io.castles.game.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Meeple {

    private final Graph.Node position;
    private final Player owner;

    public Meeple(Graph.Node position, Player owner) {
        this.position = position;
        this.owner = owner;
    }

    public static Meeple create(Player owner, Tile tile, int row, int column) {
        return new Meeple(
                new Graph.Node(tile.getX(), tile.getY(), row, column),
                owner
        );
    }
}
