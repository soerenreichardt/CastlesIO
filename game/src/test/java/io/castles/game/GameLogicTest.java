package io.castles.game;

import io.castles.core.GameMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    @Test
    void shouldSwitchToNextPlayerAfterAllPhases() {
        List<Player> players = List.of(new Player("p1"), new Player("p2"));
        var gameLogic = new GameLogic(GameMode.DEBUG, players);

        assertEquals(gameLogic.getGameState(), GameState.START);
        var activePlayer = gameLogic.getActivePlayer();
        gameLogic.nextPhase();
        assertEquals(gameLogic.getGameState(), GameState.DRAW);
        gameLogic.nextPhase();
        assertEquals(gameLogic.getGameState(), GameState.PLACE_TILE);
        gameLogic.nextPhase();
        assertEquals(gameLogic.getGameState(), GameState.PLACE_FIGURE);
        gameLogic.nextPhase();
        assertEquals(gameLogic.getGameState(), GameState.DRAW);

        var nextPlayer = players.get((players.indexOf(activePlayer) + 1) % players.size());
        assertEquals(gameLogic.getActivePlayer(), nextPlayer);
        assertNotEquals(activePlayer, nextPlayer);
    }

}