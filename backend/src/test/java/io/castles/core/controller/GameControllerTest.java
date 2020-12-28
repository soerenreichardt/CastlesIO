package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.Tile;
import io.castles.core.model.GameStateDTO;
import io.castles.core.util.JsonHelper;
import io.castles.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    Server server;

    List<Player> players;
    Game game;

    @BeforeEach
    void setup() {
        this.players = List.of(new Player("p1"), new Player("p2"));
        this.game = new Game(ImmutableGameSettings.builder().gameMode(GameMode.DEBUG).name("Test").build(), Set.copyOf(players));
    }

    @Test
    void shouldGetTileAtSpecificPosition() throws Exception {
        Mockito.when(server.gameById(any(UUID.class))).thenReturn(game);
        String tileJson = JsonHelper.serializeObject(Tile.drawStatic(Tile.TileBorder.GRAS));
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/tile", UUID.randomUUID().toString()))
                .param("x", "0")
                .param("y", "0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

    @Test
    void shouldReturnANewTile() throws Exception {
        Mockito.when(server.gameById(any(UUID.class))).thenReturn(game);
        String tileJson = JsonHelper.serializeObject(Tile.drawStatic(Tile.TileBorder.GRAS));
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/new_tile", UUID.randomUUID().toString()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

    @Test
    void shouldGetCurrentGameState() throws Exception {
        Mockito.when(server.gameById(any(UUID.class))).thenReturn(game);
        String gameStateJson = JsonHelper.serializeObject(new GameStateDTO(game.getCurrentGameState(), game.getActivePlayer()));
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/state", UUID.randomUUID().toString()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(gameStateJson));
    }

}