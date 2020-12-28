package io.castles.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.castles.core.Board;
import io.castles.core.GameMode;
import io.castles.core.Tile;
import io.castles.core.service.BoardService;
import io.castles.core.util.JsonHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BoardService boardService;

    @Test
    void shouldCreateNewBoard() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/board/new").param("game_mode", "DEBUG").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Board created")));
    }

    @Test
    void shouldGetTileAtSpecificPosition() throws Exception {
        Mockito.when(boardService.getBoard()).thenReturn(Board.create(GameMode.DEBUG));
        String tileJson = JsonHelper.serializeObject(Tile.drawStatic(Tile.TileBorder.GRAS));
        mvc.perform(MockMvcRequestBuilders
                .get("/board/tile")
                .param("x", "0")
                .param("y", "0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

    @Test
    void shouldReturnANewTile() throws Exception {
        Mockito.when(boardService.getBoard()).thenReturn(Board.create(GameMode.DEBUG));
        String tileJson = JsonHelper.serializeObject(Tile.drawStatic(Tile.TileBorder.GRAS));
        mvc.perform(MockMvcRequestBuilders.get("/board/new_tile").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

}