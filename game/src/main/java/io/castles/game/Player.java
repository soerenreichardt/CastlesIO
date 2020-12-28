package io.castles.game;

import java.util.Objects;
import java.util.UUID;

public class Player extends IdentifiableObject {

    private final String name;

    public Player(String name) {
        this.name = name;
    }

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Player)) return false;
        Player other = (Player) obj;
        return getId().equals(((Player) obj).getId()) && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name);
    }
}
