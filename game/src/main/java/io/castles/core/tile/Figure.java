package io.castles.core.tile;

import io.castles.core.graph.Graph;
import io.castles.game.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Figure {

    private final Graph.Node position;
    private final Player owner;

    public Figure(Graph.Node position, Player owner) {
        this.position = position;
        this.owner = owner;
    }

    public static Figure create(Player owner, Tile tile, int row, int column) {
        return new Figure(
                new Graph.Node(tile.getX(), tile.getY(), row, column),
                owner
        );
    }
}
