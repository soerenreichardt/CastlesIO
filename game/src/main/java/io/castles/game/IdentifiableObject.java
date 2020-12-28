package io.castles.game;

import java.util.UUID;

public abstract class IdentifiableObject {

    private final UUID id;

    public IdentifiableObject() {
        this(UUID.randomUUID());
    }

    public IdentifiableObject(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }
}
