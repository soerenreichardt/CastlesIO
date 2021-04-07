package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.events.ConnectionHandler;
import io.castles.core.events.ServerEvent;
import io.castles.core.exceptions.UnableToReconnectException;
import io.castles.core.service.ClockService;
import io.castles.core.service.PlayerEmitters;
import io.castles.core.service.ServerEventService;
import io.castles.core.service.SseEmitterService;
import io.castles.core.util.CollectingEventConsumer;
import io.castles.game.*;
import org.junit.jupiter.api.AfterEach;
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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private SseEmitterService emitterService;

    @MockBean
    private ClockService clockService;

    Player owner;
    GameLobby gameLobby;
    CollectingEventConsumer eventConsumer;

    @BeforeEach
    void setup() {
        Mockito.when(clockService.instance()).thenReturn(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
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
        var game = new Game(gameLobby.getId(), GameSettings.from(lobbySettings), Set.copyOf(gameLobby.getPlayers()), gameLobby.eventHandler());

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

    @Test
    void shouldTriggerPlayerDisconnectedEvent() {
        PlayerEmitters playerEmitters = new PlayerEmitters(gameLobby.getId(), serverEventService);
        playerEmitters.create(owner.getId());
        playerEmitters.get(owner.getId()).complete();
        playerEmitters.sendToPlayer(owner, "Foo");

        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_DISCONNECTED.name())).isTrue();
        assertThat(eventConsumer.events().get(ServerEvent.PLAYER_DISCONNECTED.name())).containsExactlyInAnyOrder(
                owner.getId().toString()
        );
    }

    @Test
    void shouldReconnectDisconnectedPlayer() throws Exception {
        var emitter = emitterService.getLobbyEmitterForPlayer(gameLobby.getId(), owner.getId());
        emitter.complete();
        emitterService.getPlayerEmitters(gameLobby.getId()).sendToPlayer(owner, "should not send");

        assertThat(emitterService.getLobbyEmitterForPlayer(gameLobby.getId(), owner.getId())).isNull();

        var urlTemplate = String.format("/lobby/%s/subscribe/%s", gameLobby.getId(), owner.getId());
        mvc.perform(MockMvcRequestBuilders.get(urlTemplate).contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());

        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_RECONNECT_ATTEMPT.name())).isTrue();
        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_RECONNECTED.name())).isTrue();
        assertThat(emitterService.getLobbyEmitterForPlayer(gameLobby.getId(), owner.getId())).isNotNull();
    }

    @Test
    void shouldNotBeAbleToReconnectAfterTimeout() throws Exception {
        var emitter = emitterService.getLobbyEmitterForPlayer(gameLobby.getId(), owner.getId());
        emitter.complete();
        emitterService.getPlayerEmitters(gameLobby.getId()).sendToPlayer(owner, "should not send");

        assertThat(emitterService.getLobbyEmitterForPlayer(gameLobby.getId(), owner.getId())).isNull();

        Mockito.reset(clockService);
        Mockito.when(clockService.instance()).thenReturn(Clock.offset(Clock.systemUTC(), Duration.ofMillis(ConnectionHandler.DISCONNECT_TIMEOUT + 1)));
        var urlTemplate = String.format("/lobby/%s/subscribe/%s", gameLobby.getId(), owner.getId());
        mvc.perform(MockMvcRequestBuilders.get(urlTemplate).contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getCause() instanceof UnableToReconnectException).isTrue())
                .andExpect(result -> assertThat(result.getResolvedException().getCause().getMessage()).contains(owner.getName()));

        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_RECONNECT_ATTEMPT.name())).isTrue();
        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_DISCONNECTED.name())).isTrue();
        assertThat(emitterService.getLobbyEmitterForPlayer(gameLobby.getId(), owner.getId())).isNull();
    }
}