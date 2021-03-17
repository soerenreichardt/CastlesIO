package io.castles.core.controller;

import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ServerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Server server;

    @Test
    void shouldCreateNewLobby() throws Exception {
        var lobbyName = "Test";
        var player = new Player("P1");
        var gameLobby = new GameLobby(lobbyName, player);
        Mockito.when(server.createGameLobby(any(String.class), any(Player.class))).thenReturn(gameLobby);
        mvc.perform(MockMvcRequestBuilders.post("/lobby").param("lobbyName", lobbyName).param("playerName", "P2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(gameLobby.getId().toString())));
    }
}