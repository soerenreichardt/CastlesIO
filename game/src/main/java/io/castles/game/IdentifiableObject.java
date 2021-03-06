package io.castles.game;

import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public abstract class IdentifiableObject {

    private final UUID id;

    public IdentifiableObject(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    protected static UUID randomUUID() {
        return UUID.randomUUID();
    }
}
