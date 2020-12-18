package io.castles.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.castles.core.Tile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("development")
class BoardControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldReturnANewTile() throws Exception {
        String tileJson = new ObjectMapper().writer().writeValueAsString(Tile.drawStatic(Tile.TileBorder.GRAS));
        mvc.perform(MockMvcRequestBuilders.get("/board/new_tile").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(tileJson));
    }

}