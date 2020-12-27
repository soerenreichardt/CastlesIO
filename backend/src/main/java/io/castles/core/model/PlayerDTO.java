package io.castles.core.model;

import io.castles.game.Player;

import java.util.UUID;

public class PlayerDTO {

    private UUID id;
    private String name;

    public PlayerDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player toPlayer() {
        return new Player(id, name);
    }
}
