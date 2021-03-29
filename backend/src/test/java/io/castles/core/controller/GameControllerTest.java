package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.model.GameStateDTO;
import io.castles.core.model.TileDTO;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.core.util.JsonHelper;
import io.castles.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Server server;

    Game game;

    @BeforeEach
    void setup() {
        GameSettings gameSettings = GameSettings.from(GameLobbySettings.builder().gameMode(GameMode.DEBUG).build());
        game = new Game(UUID.randomUUID(), gameSettings, Set.of(new Player("P1")), server.eventHandler());
        server.addGame(game);
    }

    @Test
    void shouldGetTileAtSpecificPosition() throws Exception {
        Tile tile = game.getTile(0, 0);
        String tileJson = JsonHelper.serializeObject(TileDTO.from(tile));
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/tile", game.getId().toString()))
                .param("x", "0")
                .param("y", "0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

    @Test
    void shouldReturnANewTile() throws Exception {
        Tile tile = Tile.drawStatic(TileContent.GRAS);
        String tileJson = JsonHelper.serializeObject(TileDTO.from(tile));

        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/new_tile", game.getId().toString()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(tileJson.substring(8)))); // do not check for Tile id
    }

    @Test
    void shouldGetCurrentGameState() throws Exception {
        String gameStateJson = JsonHelper.serializeObject(new GameStateDTO(game.getCurrentGameState(), game.getActivePlayer()));
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/state", game.getId().toString()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(gameStateJson));
    }

    @Disabled("waiting for other game state actions to take place")
    void shouldInsertTile() throws Exception {
        TileDTO tile = TileDTO.from(Tile.drawStatic(TileContent.GRAS));

        String tileJson = JsonHelper.serializeObject(tile);
        mvc.perform(MockMvcRequestBuilders
                .post(String.format("/game/%s/tile", game.getId().toString()))
                .param("x", "0")
                .param("y", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tileJson))
                .andExpect(status().isOk());
    }
}