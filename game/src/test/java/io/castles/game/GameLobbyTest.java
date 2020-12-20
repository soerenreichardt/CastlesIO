package io.castles.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class GameLobbyTest {

    GameLobby gameLobby;

    @BeforeEach
    void setup() {
        this.gameLobby = new GameLobby();
    }

    @Test
    void shouldAddAndRemovePlayers() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        assertEquals(0, gameLobby.getNumPlayers());

        gameLobby.addPlayer(p1);
        assertEquals(1, gameLobby.getNumPlayers());
        gameLobby.addPlayer(p2);
        assertEquals(2, gameLobby.getNumPlayers());
        gameLobby.removePlayer(p1);
        assertEquals(1, gameLobby.getNumPlayers());
        gameLobby.removePlayer(p2);
        assertEquals(0, gameLobby.getNumPlayers());
    }

    @Test
    void canOnlyStartWithCorrectPlayerNumber() {
        for (int i = 0; i < GameLobby.MIN_PLAYERS - 1; i++) {
            gameLobby.addPlayer(new Player("" + i));
            assertFalse(gameLobby.canStart());
        }

        for (int i = GameLobby.MIN_PLAYERS; i <= GameLobby.MAX_PLAYERS; i++) {
            gameLobby.addPlayer(new Player("" + i));
            assertTrue(gameLobby.canStart());
        }
    }

    @Test
    void shouldThrowWhenRemovingNonExistentPlayer() {
        assertThatThrownBy(() -> gameLobby.removePlayer(new Player("p1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("was not found");
    }

    @Test
    void shouldThrowWhenAddingPlayerWhileLobbyIsFull() {
        for (int i = 0; i < GameLobby.MAX_PLAYERS; i++) {
            gameLobby.addPlayer(new Player("" + i));
        }
        assertThatThrownBy(() -> gameLobby.addPlayer(new Player("foo")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maximum number of players reached");
    }
}
