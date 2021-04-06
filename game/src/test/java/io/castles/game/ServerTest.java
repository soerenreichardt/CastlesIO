package io.castles.game;

import io.castles.core.GameMode;
import io.castles.core.Visibility;
import io.castles.exceptions.UnableToStartException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServerTest {

    static final Server server = Server.getInstance();
    private static final Player DEFAULT_PLAYER = new Player("P1");

    @AfterEach
    void tearDown() {
        server.reset();
    }

    @Test
    void shouldCreateAndLookupNamedLobbies() {
        assertEquals(0, server.getActiveGameLobbies().size());
        GameLobby gameLobby = server.createGameLobby("Test", DEFAULT_PLAYER);
        assertEquals(1, server.getActiveGameLobbies().size());
        assertEquals(gameLobby, server.gameLobbyById(gameLobby.getId()));
    }

    @Test
    void shouldStartAGameAndRemoveFromLobbies() {
        GameLobby gameLobby = server.createGameLobby("Test", DEFAULT_PLAYER);
        gameLobby.setGameMode(GameMode.DEBUG);

        gameLobby.addPlayer(new Player("p1"));
        gameLobby.addPlayer(new Player("p2"));

        assertEquals(1, server.getActiveGameLobbies().size());
        assertEquals(0, server.getActiveGames().size());

        Game game = server.startGame(gameLobby.getId());

        assertEquals(0, server.getActiveGameLobbies().size());
        assertEquals(1, server.getActiveGames().size());

        assertNotNull(game);
        assertEquals(game.getCurrentGameState(), GameState.DRAW);
    }

    @Test
    void shouldListPublicLobbies() {
        GameLobby lobby1 = server.createGameLobby("lobby1", new Player("owner"));
        GameLobby lobby2 = server.createGameLobby("lobby2", new Player("owner"));
        lobby2.getLobbySettings().setVisibility(Visibility.PRIVATE);
        GameLobby lobby3 = server.createGameLobby("lobby3", new Player("owner"));

        assertThat(server.publicGameLobbies()).containsExactlyInAnyOrderElementsOf(List.of(lobby1, lobby3));
    }

    @Test
    void shouldNotRemoveLobbyOnUnsuccessfulGameStart() {
        var gameLobby = server.createGameLobby("Test", new Player("owner"));
        assertThatThrownBy(() -> server.startGame(gameLobby.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(UnableToStartException.class);

        assertThat(server.getActiveGames()).isEmpty();
        assertThat(server.getActiveGameLobbies()).contains(gameLobby);
    }
}