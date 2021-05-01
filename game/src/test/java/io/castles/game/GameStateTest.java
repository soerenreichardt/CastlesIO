package io.castles.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void shouldStoreFlags() {
        assertTrue(GameState.START.isInitializationStep());
        assertFalse(GameState.START.isSkippable());

        assertFalse(GameState.DRAW.isInitializationStep());
        assertFalse(GameState.DRAW.isSkippable());

        assertFalse(GameState.PLACE_TILE.isInitializationStep());
        assertFalse(GameState.PLACE_TILE.isSkippable());

        assertFalse(GameState.PLACE_FIGURE.isInitializationStep());
        assertTrue(GameState.PLACE_FIGURE.isSkippable());

        assertFalse(GameState.NEXT_PLAYER.isInitializationStep());
        assertFalse(GameState.NEXT_PLAYER.isSkippable());

        assertTrue(GameState.GAME_END.isEndStep());
    }

    @Test
    void shouldAdvance() {
        assertEquals(GameState.DRAW, GameState.START.advance());
        assertEquals(GameState.PLACE_TILE, GameState.DRAW.advance());
        assertEquals(GameState.PLACE_FIGURE, GameState.PLACE_TILE.advance());
        assertEquals(GameState.NEXT_PLAYER, GameState.PLACE_FIGURE.advance());
        assertEquals(GameState.DRAW, GameState.NEXT_PLAYER.advance());
    }

    @ParameterizedTest
    @EnumSource(value = GameState.class)
    void shouldAdvanceToEndGame(GameState gameState) {
        gameState.endGame();
        assertEquals(GameState.GAME_END, gameState.advance());
    }

}