package io.castles.game;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void shouldBeEqualInNameAndId() {
        var id = UUID.randomUUID();
        var p1 = new Player(id, "P1");
        var p2 = new Player(id, "P2");

        assertNotEquals(p2, p1);

        p1 = new Player(UUID.randomUUID(), "P1");
        p2 = new Player(UUID.randomUUID(), "P1");

        assertNotEquals(p2, p1);
    }
}