package io.castles.core.model;

import io.castles.game.Player;
import lombok.Value;

import java.util.UUID;

@Value
public class PlayerDTO {

    UUID id;
    String name;

    public Player toPlayer() {
        return new Player(id, name);
    }
}
