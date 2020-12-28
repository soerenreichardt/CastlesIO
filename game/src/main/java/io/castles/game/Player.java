package io.castles.game;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class Player extends IdentifiableObject {

    String name;

    public Player(String name) {
        this.name = name;
    }

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
    }
}
