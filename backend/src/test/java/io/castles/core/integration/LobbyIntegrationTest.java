package io.castles.core.integration;

import io.castles.core.model.dto.LobbySettingsDTO;
import io.castles.core.model.dto.PlayerIdentificationDTO;
import io.castles.core.service.ClockService;
import io.castles.core.service.ServerEventService;
import io.castles.core.util.CollectingEventConsumer;
import io.castles.core.util.JsonHelper;
import io.castles.game.GameLobbySettings;
import io.castles.game.Server;
import io.castles.game.events.GameEvent;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class LobbyIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ServerEventService serverEventService;

    @Autowired
    Server server;

    @MockBean
    ClockService clockService;

    CollectingEventConsumer eventConsumer;

    @BeforeEach
    void setup() {
        Mockito.when(clockService.instance()).thenCallRealMethod();
        eventConsumer = new CollectingEventConsumer();
        server.eventHandler().registerGlobalEventConsumer(eventConsumer);
        serverEventService.registerEventConsumerSupplier(id -> eventConsumer);
    }

    @Test
    void shouldHandleLobbyActions() throws Exception {
        var lobbyId = createLobby();
        joinPlayer(lobbyId, "Player2");
        joinPlayer(lobbyId, "Player3");
        joinPlayer(lobbyId, "Player4");
        removePlayer(lobbyId, "Player4");

        var gameLobby = server.gameLobbyById(lobbyId);
        assertThat(gameLobby.getNumPlayers()).isEqualTo(3);
        assertThat(eventConsumer.events().keySet()).containsExactlyInAnyOrder(
                GameEvent.LOBBY_CREATED.name(),
                GameEvent.SETTINGS_CHANGED.name(),
                GameEvent.PLAYER_ADDED.name(),
                GameEvent.PLAYER_REMOVED.name()
        );
    }

    private UUID createLobby() throws Exception {
        var gameLobbySettings = LobbySettingsDTO.from(GameLobbySettings.builder().build());
        var defaultSettings = JsonHelper.serializeObject(gameLobbySettings);
        var result = mvc.perform(MockMvcRequestBuilders.post("/lobby")
                .param("lobbyName", "TestLobby")
                .param("playerName", "Player1")
                .content(defaultSettings)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonHelper.deserializeObject(result, PlayerIdentificationDTO.class).getLobbyId();
    }

    private void joinPlayer(UUID lobbyId, String playerName) throws Exception {
        String playerJoinUrl = String.format("/lobby/%s/join", lobbyId);
        mvc.perform(MockMvcRequestBuilders.put(playerJoinUrl)
                .param("playerName", playerName))
                .andExpect(status().isOk());
    }

    private void removePlayer(UUID lobbyId, String playerName) throws Exception {
        String playerRemoveUrl = String.format("/lobby/%s/leave", lobbyId);
        var players = server.gameLobbyById(lobbyId).getPlayers();
        var playerToBeRemoved = players
                .stream()
                .filter(player -> player.getName().equals(playerName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Expected player `%s` to be part of player list %s", playerName, players)));
        mvc.perform(MockMvcRequestBuilders.delete(playerRemoveUrl)
                .param("playerId", playerToBeRemoved.getId().toString()))
                .andExpect(status().isOk());
    }
}
