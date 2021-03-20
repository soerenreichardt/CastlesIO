package io.castles.core.controller;

import io.castles.core.model.LobbySettingsDTO;
import io.castles.core.service.SseEmitterService;
import io.castles.core.util.CollectingEventConsumer;
import io.castles.core.util.JsonHelper;
import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.Server;
import io.castles.game.events.Event;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private SseEmitterService emitterService;

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
        var lobbyName = "Test";
        var player = new Player("P1");
        var gameLobby = new GameLobby(lobbyName, player);
        var gameLobbySettings = GameLobbySettings.builder().build();
        var defaultSettings = JsonHelper.serializeObject(gameLobbySettings);
        var eventConsumer = new CollectingEventConsumer();

        Mockito.when(server.createGameLobby(any(String.class), any(Player.class))).thenReturn(gameLobby);
        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);
        Mockito.when(emitterService.getLobbyEmitterForPlayer(any(UUID.class), any(UUID.class))).thenReturn(new SseEmitter());
        Mockito.when(emitterService.eventConsumerForLobby(any(GameLobby.class))).thenReturn(eventConsumer);
        mvc.perform(MockMvcRequestBuilders.post("/lobby")
                .param("lobbyName", lobbyName)
                .param("playerName", "P1")
                .content(defaultSettings)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(gameLobby.getId().toString())));

        assertThat(eventConsumer.events()).isEqualTo(
                Map.of(
                        Event.PLAYER_ADDED.name(), List.of(player.toString()),
                        Event.SETTINGS_CHANGED.name(), List.of(gameLobbySettings.toString())
                )
        );
    }
}