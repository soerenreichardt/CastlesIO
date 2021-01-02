package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.util.JsonHelper;
import io.castles.game.*;
import io.castles.game.GameSettings.GameSettingsBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class LobbyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Server server;

    @Test
    void shouldJoinAsPlayer() throws Exception {
        var gameLobby = new GameLobby("Test");
        var player = new Player("p1");

        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);
        var urlTemplate = String.format("/lobby/%s/join", gameLobby.getId());

        assertEquals(0, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate).param("playerName", "Foo Bar"))
                .andExpect(status().isOk());
        assertEquals(1, gameLobby.getNumPlayers());
    }

    @Test
    void shouldRemovePlayer() throws Exception {
        var gameLobby = new GameLobby("Test");
        var player = new Player("p1");
        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);

        gameLobby.addPlayer(player);

        var urlTemplate = String.format("/lobby/%s/leave", gameLobby.getId());

        assertEquals(1, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.delete(urlTemplate).param("playerId", player.getId().toString()))
                .andExpect(status().isOk());
        assertEquals(0, gameLobby.getNumPlayers());
    }

    @Test
    void shouldStartAGame() throws Exception {
        var gameLobby = new GameLobby("Test");
        for (int i = 0; i < GameLobby.MIN_PLAYERS; i++) {
            gameLobby.addPlayer(new Player("" + i));
        }

        Game game = new Game(GameSettings.builder().gameMode(GameMode.DEBUG).name("game").build(), Set.of(new Player("foo")));
        Mockito.when(server.startGame(any(UUID.class))).thenReturn(game);

        var urlTemplate = String.format("/lobby/%s/start", gameLobby.getId());

        assertEquals(0, server.getActiveGames().size());
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(game.getId().toString())));
        verify(server, times(1)).startGame(gameLobby.getId());
    }

    // TODO: exception handling
}