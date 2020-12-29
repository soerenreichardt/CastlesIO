package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.Tile;
import io.castles.core.model.GameStateDTO;
import io.castles.core.model.TileDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Tile tile = game.getTile(0, 0);
        String tileJson = JsonHelper.serializeObject(new TileDTO(tile.getId(), tile.getTileBorders()));
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/tile", UUID.randomUUID().toString()))
                .param("x", "0")
                .param("y", "0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

    @Test
    void shouldReturnANewTile() throws Exception {
        Game gameMock = Mockito.mock(Game.class);
        Mockito.when(server.gameById(any(UUID.class))).thenReturn(gameMock);
        Tile tile = Tile.drawStatic(Tile.TileBorder.GRAS);
        Mockito.when(gameMock.getNewTile()).thenReturn(tile);
        String tileJson = JsonHelper.serializeObject(new TileDTO(tile.getId(), tile.getTileBorders()));
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

    @Test
    void shouldInsertTile() throws Exception {
        Mockito.when(server.gameById(any(UUID.class))).thenReturn(game);
        TileDTO tile = new TileDTO(UUID.randomUUID(), Tile.drawStatic(Tile.TileBorder.GRAS).getTileBorders());

        String tileJson = JsonHelper.serializeObject(tile);
        mvc.perform(MockMvcRequestBuilders
                .post(String.format("/game/%s/tile", UUID.randomUUID().toString()))
                .param("x", "0")
                .param("y", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tileJson))
                .andExpect(status().isOk());

        assertEquals(tile.toTile(), game.getTile(0, 1));
    }
}