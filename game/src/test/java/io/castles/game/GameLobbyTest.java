package io.castles.game;

import io.castles.game.events.EventHandler;
import io.castles.game.events.GameEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class GameLobbyTest {

    GameLobby gameLobby;

    @BeforeEach
    void setup() {
        this.gameLobby = new GameLobby("Test", new Player("owner"), Server.getInstance().eventHandler());
        this.gameLobby.initialize();
    }

    @Test
    void shouldAddAndRemovePlayers() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        assertEquals(1, gameLobby.getNumPlayers());

        gameLobby.addPlayer(p1);
        assertEquals(2, gameLobby.getNumPlayers());
        gameLobby.addPlayer(p2);
        assertEquals(3, gameLobby.getNumPlayers());
        gameLobby.removePlayer(p1);
        assertEquals(2, gameLobby.getNumPlayers());
        gameLobby.removePlayer(p2.getId());
        assertEquals(1, gameLobby.getNumPlayers());
    }

    @Test
    void canOnlyStartWithCorrectPlayerNumber() {
        for (int i = 1; i < GameLobby.MIN_PLAYERS - 1; i++) {
            gameLobby.addPlayer(new Player("" + i));
            assertFalse(gameLobby.canStart());
        }

        for (int i = GameLobby.MIN_PLAYERS; i <= gameLobby.getMaxPlayers(); i++) {
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
    void shouldThrowWhenRemovingNonExistentPlayerById() {
        assertThatThrownBy(() -> gameLobby.removePlayer(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("was not found");
    }

    @Test
    void shouldThrowWhenAddingPlayerWhileLobbyIsFull() {
        for (int i = 1; i < gameLobby.getMaxPlayers(); i++) {
            gameLobby.addPlayer(new Player("" + i));
        }
        assertThatThrownBy(() -> gameLobby.addPlayer(new Player("foo")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maximum number of players reached");
    }

    @Test
    void shouldThrowWhenEventIsTriggeredOnUninitializedLobby() {
        var gameLobby = new GameLobby("Test", new Player("p1"), new EventHandler());
        assertThatThrownBy(() -> gameLobby.triggerEvent(GameEvent.LOBBY_CREATED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("uninitialized class");
    }
}
