package io.castles.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BoardFactoryBeanTest {

    @Autowired
    private Board board;

    @Test
    void injectsBoard() {
        assertNotNull(board);
        assertNotNull(board.getTile(0, 0));
    }

}