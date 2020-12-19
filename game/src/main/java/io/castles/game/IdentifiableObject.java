package io.castles.game;

import java.util.UUID;

public abstract class IdentifiableObject {

    private final UUID id;

    public IdentifiableObject() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }
}
