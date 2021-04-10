package io.castles.game;

import io.castles.core.GameMode;
import io.castles.game.events.EventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GameLogicTest {

    List<Player> players;
    GameLogic gameLogic;

    @BeforeEach
    void setup() {
        players = List.of(new Player("p1"), new Player("p2"));
        gameLogic = new GameLogic(UUID.randomUUID(), GameMode.DEBUG, players, new EventHandler());
    }

    @Test
    void shouldSwitchToNextPlayerAfterAllPhases() {
        assertEquals(gameLogic.getGameState(), GameState.START);
        var activePlayer = gameLogic.getActivePlayer();
        gameLogic.initialize();
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

    @Test
    void shouldResetGameLogic() {
        gameLogic.initialize();
        gameLogic.nextPhase();
        assertThat(gameLogic.getGameState()).isNotEqualTo(GameState.DRAW);
        gameLogic.restart();
        assertThat(gameLogic.getGameState()).isEqualTo(GameState.DRAW);
    }

}