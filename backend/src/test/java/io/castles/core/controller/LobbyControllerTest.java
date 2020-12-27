package io.castles.core.controller;

import io.castles.core.util.JsonHelper;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        var playerJson = JsonHelper.serializeObject(player);

        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);
        var urlTemplate = String.format("/lobby/%s/join", gameLobby.getId());

        assertEquals(0, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(playerJson))
                .andExpect(status().isOk());
        assertEquals(1, gameLobby.getNumPlayers());
    }

    @Test
    void shouldRemovePlayer() throws Exception {
        var gameLobby = new GameLobby("Test");
        var player = new Player("p1");
        var playerJson = JsonHelper.serializeObject(player);
        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);

        gameLobby.addPlayer(player);

        var urlTemplate = String.format("/lobby/%s/leave", gameLobby.getId());

        assertEquals(1, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.delete(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(playerJson))
                .andExpect(status().isOk());
        assertEquals(0, gameLobby.getNumPlayers());
    }

    @Test
    void shouldStartAGame() throws Exception {
        var gameLobby = new GameLobby("Test");
        for (int i = 0; i < GameLobby.MIN_PLAYERS; i++) {
            gameLobby.addPlayer(new Player("" + i));
        }

        var urlTemplate = String.format("/lobby/%s/start", gameLobby.getId());

        assertEquals(0, server.getActiveGames().size());
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate))
                .andExpect(status().isOk());
        verify(server, times(1)).startGame(gameLobby.getId());
    }

    // TODO: exception handling
}