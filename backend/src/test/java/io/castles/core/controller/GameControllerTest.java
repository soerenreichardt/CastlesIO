package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.model.dto.GameStateDTO;
import io.castles.core.model.dto.PlayerDTO;
import io.castles.core.model.dto.TileDTO;
import io.castles.core.service.GameService;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.core.util.JsonHelper;
import io.castles.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    private Server server;

    @MockBean
    private GameService gameService;

    Game game;

    @BeforeEach
    void setup() {
        GameSettings gameSettings = GameSettings.from(GameLobbySettings.builder().gameMode(GameMode.DEBUG).build());
        game = new Game(UUID.randomUUID(), "Just some lobby name", gameSettings, Set.of(new Player("P1")), server.eventHandler());
        server.addGame(game);
    }

    @Test
    void shouldGetTileAtSpecificPosition() throws Exception {
        Tile tile = game.getTile(0, 0);
        String tileJson = JsonHelper.serializeObject(TileDTO.from(tile));
        Mockito.when(gameService.gameById(any())).thenReturn(game);
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/tile", game.getId().toString()))
                .param("x", "0")
                .param("y", "0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

    @Test
    void shouldReturnANewTile() throws Exception {
        Tile tile = Tile.drawStatic(TileContent.GRAS);
        String tileJson = JsonHelper.serializeObject(TileDTO.from(tile));

        Mockito.when(gameService.getNewTile(any(), any())).thenReturn(tile);
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/new_tile", game.getId().toString()))
                .param("playerId", UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson)); // do not check for Tile id
    }

    @Test
    void shouldReturnDrawnTile() throws Exception {
        Player activePlayer = game.getActivePlayer();

        Tile drawnTile = Tile.drawStatic(TileContent.GRAS);
        String tileJson = JsonHelper.serializeObject(TileDTO.from(drawnTile));

        Mockito.when(gameService.getDrawnTile(any(), any())).thenReturn(drawnTile);
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/drawn_tile", game.getId().toString()))
                .param("playerId", activePlayer.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

    @Test
    void shouldGetCurrentGameState() throws Exception {
        String gameStateJson = JsonHelper.serializeObject(new GameStateDTO(game.getCurrentGameState(), PlayerDTO.from(game.getActivePlayer())));
        Mockito.when(gameService.gameById(any())).thenReturn(game);
        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/game/%s/state", game.getId().toString()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(gameStateJson));
    }

    @Test
    void shouldInsertTile() throws Exception {
        TileDTO tile = TileDTO.from(Tile.drawStatic(TileContent.GRAS));

        String tileJson = JsonHelper.serializeObject(tile);
        mvc.perform(MockMvcRequestBuilders
                .post(String.format("/game/%s/tile", game.getId().toString()))
                .param("playerId", game.getActivePlayer().getId().toString())
                .param("x", "0")
                .param("y", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tileJson))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRestartGame() throws Exception {
        Mockito.when(gameService.gameById(any())).thenReturn(game);
        mvc.perform(MockMvcRequestBuilders
                .post(String.format("/game/%s/restart", game.getId().toString()))
                .param("playerId", game.getActivePlayer().getId().toString())
                .param("x", "0")
                .param("y", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPlaceFigure() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post(String.format("/game/%s/figure", game.getId().toString()))
                .param("playerId", game.getActivePlayer().getId().toString())
                .param("x", "0")
                .param("y", "1")
                .param("row", "0")
                .param("column", "0"))
                .andExpect(status().isOk());
    }
}