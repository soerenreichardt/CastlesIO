package io.castles.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    static final Server server = Server.getInstance();

    @Test
    void shouldCreateAndLookupNamedLobbies() {
        assertEquals(0, server.getActiveGameLobbies().size());
        GameLobby gameLobby = server.createGameLobby("Test");
        assertEquals(1, server.getActiveGameLobbies().size());
        assertEquals(gameLobby, server.gameLobbyById(gameLobby.getId()));
    }

    @Test
    void shouldStartAGameAndRemoveFromLobbies() {
        GameLobby gameLobby = server.createGameLobby("Test");
        gameLobby.addPlayer(new Player("p1"));
        gameLobby.addPlayer(new Player("p2"));

        assertEquals(1, server.getActiveGameLobbies().size());
        assertEquals(0, server.getActiveGames().size());

        Game game = server.startGame(gameLobby.getId());

        assertEquals(0, server.getActiveGameLobbies().size());
        assertEquals(1, server.getActiveGames().size());

        assertNotNull(game);
        assertEquals(game.getCurrentGameState(), GameState.START);

    }
}