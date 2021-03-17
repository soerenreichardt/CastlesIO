package io.castles.game;

import io.castles.core.GameMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    static final Server server = Server.getInstance();

    @AfterEach
    void tearDown() {
        server.reset();
    }

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
        gameLobby.setGameMode(GameMode.DEBUG);

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

    @Test
    void shouldListPublicLobbies() {
        GameLobby lobby1 = server.createGameLobby("lobby1", GameLobby.Visibility.PUBLIC);
        GameLobby lobby2 = server.createGameLobby("lobby2", GameLobby.Visibility.PRIVATE);
        GameLobby lobby3 = server.createGameLobby("lobby3", GameLobby.Visibility.PUBLIC);
        
        assertThat(server.publicGameLobbies()).containsExactlyInAnyOrderElementsOf(List.of(lobby1, lobby3));
    }
}