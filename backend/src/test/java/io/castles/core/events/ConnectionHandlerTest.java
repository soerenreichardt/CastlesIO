package io.castles.core.events;

import io.castles.core.service.ClockService;
import io.castles.core.service.PlayerEmitters;
import io.castles.core.service.ServerEventService;
import io.castles.game.Player;
import io.castles.game.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionHandlerTest {

    ServerEventService serverEventService;

    @Mock
    ClockService clockService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.serverEventService = new ServerEventService(Server.getInstance());
    }

    @Test
    void shouldNotIterateThroughDisconnectedOrTimeoutPlayers() {
        Mockito.when(clockService.instance()).thenReturn(Clock.systemUTC());
        var playerEmitters = new PlayerEmitters(UUID.randomUUID(), serverEventService);
        var connectionHandler = new ConnectionHandler(playerEmitters, clockService);

        List<Player> players = List.of(new Player("1"), new Player("2"), new Player("3"));

        Player disconnectedPlayer = players.get(1);
        connectionHandler.playerDisconnected(disconnectedPlayer);

        Set<Player> availablePlayers = new HashSet<>();
        connectionHandler.forEachConnectedPlayer(players, availablePlayers::add);

        assertThat(availablePlayers).doesNotContain(disconnectedPlayer);

        Mockito.reset(clockService);
        Mockito.when(clockService.instance()).thenReturn(Clock.offset(Clock.systemUTC(), Duration.ofMillis(ConnectionHandler.DISCONNECT_TIMEOUT)));

        connectionHandler.checkDisconnectionTimeout();
        assertThat(connectionHandler.timeoutPlayers()).contains(disconnectedPlayer);

        availablePlayers.clear();
        connectionHandler.forEachConnectedPlayer(players, availablePlayers::add);

        assertThat(availablePlayers).doesNotContain(disconnectedPlayer);
    }

}