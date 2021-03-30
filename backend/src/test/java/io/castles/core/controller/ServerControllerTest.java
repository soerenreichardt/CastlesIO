package io.castles.core.controller;

import io.castles.core.model.dto.LobbySettingsDTO;
import io.castles.core.model.dto.PlayerIdentificationDTO;
import io.castles.core.util.JsonHelper;
import io.castles.game.GameLobbySettings;
import io.castles.game.Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
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
    void shouldGetDefaultSettings() throws Exception {
        var lobbySettingsDefaults = GameLobbySettings.builder().build();
        var defaultLobbySettingsDTO = LobbySettingsDTO.from(lobbySettingsDefaults);
        String settingsString = mvc.perform(MockMvcRequestBuilders.get("/settings"))
                .andReturn().getResponse().getContentAsString();
        assertThat(settingsString).isEqualTo(JsonHelper.serializeObject(defaultLobbySettingsDTO));
    }

    @Test
    void shouldCreateNewLobby() throws Exception {
        var gameLobbySettings = GameLobbySettings.builder().build();
        var defaultSettings = JsonHelper.serializeObject(gameLobbySettings);

        String result = mvc.perform(MockMvcRequestBuilders.post("/lobby")
                .param("lobbyName", "Test")
                .param("playerName", "P1")
                .content(defaultSettings)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(server.getActiveGameLobbies().size()).isEqualTo(1);
        var playerIdentificationDTO = JsonHelper.deserializeObject(result, PlayerIdentificationDTO.class);
        assertThat(server.gameLobbyById(playerIdentificationDTO.getLobbyId()).getName()).isEqualTo("Test");
    }
}