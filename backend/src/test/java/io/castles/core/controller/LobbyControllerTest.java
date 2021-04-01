package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.service.ServerEventService;
import io.castles.core.service.SseEmitterService;
import io.castles.core.util.CollectingEventConsumer;
import io.castles.game.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class LobbyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Server server;

    @Autowired
    private ServerEventService serverEventService;

    Player owner;
    GameLobby gameLobby;
    CollectingEventConsumer eventConsumer;

    @BeforeEach
    void setup() {
        eventConsumer = new CollectingEventConsumer();
        serverEventService.registerEventConsumerSupplier(id -> eventConsumer);

        owner = new Player("Owner");
        gameLobby = server.createGameLobby("Test", owner);
    }

    @AfterEach
    void tearDown() {
        eventConsumer.reset();
    }

    @Test
    void shouldGetPublicLobbyInfo() throws Exception {
        var urlTemplate = String.format("/lobby/%s/info", gameLobby.getId());

        mvc.perform(MockMvcRequestBuilders.get(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(String.format("\"name\":\"%s\"", gameLobby.getName()))))
                .andExpect(content().string(containsString(String.format("\"numPlayers\":%s", gameLobby.getNumPlayers()))))
                .andExpect(content().string(containsString(String.format("\"maxPlayers\":%s", gameLobby.getMaxPlayers()))));
    }

    @Test
    void shouldJoinAsPlayer() throws Exception {
        var urlTemplate = String.format("/lobby/%s/join", gameLobby.getId());

        assertEquals(1, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate).param("playerName", "Foo Bar"))
                .andExpect(status().isOk());
        assertEquals(2, gameLobby.getNumPlayers());
    }

    @Test
    void shouldRemovePlayer() throws Exception {
        var urlTemplate = String.format("/lobby/%s/leave", gameLobby.getId());

        assertEquals(1, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.delete(urlTemplate).param("playerId", owner.getId().toString()))
                .andExpect(status().isOk());
        assertEquals(0, gameLobby.getNumPlayers());
    }

    @Test
    void shouldStartAGame() throws Exception {
        for (int i = 0; i < GameLobby.MIN_PLAYERS; i++) {
            gameLobby.addPlayer(new Player("" + i));
        }

        var lobbySettings = gameLobby.getLobbySettings();
        lobbySettings.setGameMode(GameMode.DEBUG);
        var game = new Game(gameLobby.getId(), GameSettings.from(lobbySettings), Set.of(new Player("foo")), gameLobby.eventHandler());

        var urlTemplate = String.format("/lobby/%s/start", gameLobby.getId());

        assertEquals(0, server.getActiveGames().size());
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(game.getId().toString())));
        assertEquals(1, server.getActiveGames().size());
    }

    @Test
    void shouldBeAbleToSubscribeToSseEmitter() throws Exception {
        var urlTemplate = String.format("/lobby/%s/subscribe/%s", gameLobby.getId(), owner.getId());

        mvc.perform(MockMvcRequestBuilders.get(urlTemplate).contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());

    }

    static void assertEvents(Map<String, List<String>> actual, Map<ServerEvent, List<String>> expected) {
        assertThat(actual).isEqualTo(
                expected
                        .entrySet()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        entry -> entry.getKey().name(),
                                        Map.Entry::getValue
                                )
                        )
        );
    }
}