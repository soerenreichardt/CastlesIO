package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.events.ConnectionHandler;
import io.castles.core.events.ServerEvent;
import io.castles.core.service.ClockService;
import io.castles.core.service.ServerEventService;
import io.castles.core.service.SseEmitterService;
import io.castles.core.util.CollectingEventConsumer;
import io.castles.game.Game;
import io.castles.game.Player;
import io.castles.game.Server;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerReconnectionTest {

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

    Game game;
    CollectingEventConsumer eventConsumer;

    @BeforeEach
    void setup() {
        Mockito.when(clockService.instance()).thenReturn(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
        eventConsumer = new CollectingEventConsumer();
        serverEventService.registerEventConsumerSupplier(id -> eventConsumer);

        var owner = new Player("Owner");
        var player2 = new Player("P2");
        var gameLobby = server.createGameLobby("Test", owner);
        gameLobby.addPlayer(player2);
        gameLobby.setGameMode(GameMode.DEBUG);
        game = server.startGame(gameLobby.getId());
    }

    @Test
    void shouldReconnectDisconnectedPlayer() throws Exception {
        var player = game.getPlayers().get(0);
        var emitter = emitterService.getGameObjectEmitterForPlayer(game.getId(), player.getId());
        emitter.complete();
        emitterService.getPlayerEmitters(game.getId()).sendToPlayer(player, "should not send");

        assertThat(emitterService.getGameObjectEmitterForPlayer(game.getId(), player.getId())).isNull();

        var urlTemplate = String.format("/game/%s/subscribe/%s", game.getId(), player.getId());
        mvc.perform(MockMvcRequestBuilders.get(urlTemplate).contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());

        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_RECONNECT_ATTEMPT.name())).isTrue();
        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_RECONNECTED.name())).isTrue();
        assertThat(emitterService.getGameObjectEmitterForPlayer(game.getId(), player.getId())).isNotNull();
    }

    @Test
    void shouldNotBeAbleToReconnectAfterTimeout() throws Exception {
        var player = game.getPlayers().get(0);
        var emitter = emitterService.getGameObjectEmitterForPlayer(game.getId(), player.getId());
        emitter.complete();
        emitterService.getPlayerEmitters(game.getId()).sendToPlayer(player, "should not send");

        assertThat(emitterService.getGameObjectEmitterForPlayer(game.getId(), player.getId())).isNull();

        Mockito.reset(clockService);
        Mockito.when(clockService.instance()).thenReturn(Clock.offset(Clock.systemUTC(), Duration.ofMillis(ConnectionHandler.DISCONNECT_TIMEOUT + 1)));
        var urlTemplate = String.format("/game/%s/subscribe/%s", game.getId(), player.getId());
        mvc.perform(MockMvcRequestBuilders.get(urlTemplate).contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains(player.getName()));

        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_RECONNECT_ATTEMPT.name())).isTrue();
        assertThat(eventConsumer.events().containsKey(ServerEvent.PLAYER_DISCONNECTED.name())).isTrue();
        assertThat(emitterService.getGameObjectEmitterForPlayer(game.getId(), player.getId())).isNull();
    }
}
