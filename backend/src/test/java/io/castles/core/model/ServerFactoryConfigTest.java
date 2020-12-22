package io.castles.core.model;

import io.castles.game.Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ServerFactoryConfigTest {

    @Autowired
    private Server server;

    @Test
    void injectsBoard() {
        assertNotNull(server);
    }

}