package io.castles.core.model;

import io.castles.core.Board;
import io.castles.core.GameMode;
import io.castles.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BoardFactoryConfigTest {

    @Autowired
    private BeanFactory beanFactory;

    Board board;

    @BeforeEach
    void setup() {
        board = beanFactory.getBean(Board.class, GameMode.DEBUG);
    }

    @Test
    void injectsBoard() {
        assertNotNull(board);
        var tile = board.getTile(0, 0);
        assertNotNull(tile);
        for (Tile.TileBorder tileBorder : tile.getTileBorders()) {
            assertEquals(Tile.TileBorder.GRAS, tileBorder);
        }
    }

}