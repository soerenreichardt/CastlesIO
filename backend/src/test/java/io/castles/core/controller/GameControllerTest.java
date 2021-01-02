package io.castles.core.controller;

import io.castles.core.tile.Tile;
import io.castles.core.model.GameStateDTO;
import io.castles.core.model.TileDTO;
import io.castles.core.tile.TileContent;
import io.castles.core.util.JsonHelper;
import io.castles.game.*;
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

    @Autowired
    Game game;

    @Test
    void shouldGetTileAtSpecificPosition() throws Exception {
        Mockito.when(server.gameById(any(UUID.class))).thenReturn(game);
        Mockito.when(game.getTile(0, 0)).thenReturn(Tile.drawStatic(TileContent.GRAS));
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
        Mockito.when(server.gameById(any(UUID.class))).thenReturn(game);
        Tile tile = Tile.drawStatic(TileContent.GRAS);
        Mockito.when(game.getNewTile()).thenReturn(tile);
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
        Mockito.when(game.getCurrentGameState()).thenReturn(GameState.START);
        Mockito.when(game.getActivePlayer()).thenReturn(new Player("P1"));
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
        Mockito.when(game.getCurrentGameState()).thenReturn(GameState.PLACE_TILE);
        TileDTO tile = new TileDTO(UUID.randomUUID(), Tile.drawStatic(TileContent.GRAS).getTileBorders());
        Mockito.when(game.getTile(0, 1)).thenReturn(tile.toTile());

        String tileJson = JsonHelper.serializeObject(tile);
        mvc.perform(MockMvcRequestBuilders
                .post(String.format("/game/%s/tile", UUID.randomUUID().toString()))
                .param("x", "0")
                .param("y", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tileJson))
                .andExpect(status().isOk());
    }
}