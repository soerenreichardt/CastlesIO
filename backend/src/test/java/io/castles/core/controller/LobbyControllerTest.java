package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.service.ServerEventService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.*;
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

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private SseEmitterService emitterService;

    @Test
    void shouldGetPublicLobbyInfo() throws Exception {
        var owner = new Player("Owner");
        var gameLobby = new GameLobby("Test", owner);

        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);
        var urlTemplate = String.format("/lobby/%s/info", gameLobby.getId());

        mvc.perform(MockMvcRequestBuilders.get(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(String.format("\"name\":\"%s\"", gameLobby.getName()))))
                .andExpect(content().string(containsString(String.format("\"numPlayers\":%s", gameLobby.getNumPlayers()))))
                .andExpect(content().string(containsString(String.format("\"maxPlayers\":%s", gameLobby.getMaxPlayers()))));
    }

    @Test
    void shouldJoinAsPlayer() throws Exception {
        var owner = new Player("Owner");
        var gameLobby = new GameLobby("Test", owner);
        var sseEmitter = new SseEmitter();
        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);
        Mockito.when(emitterService.getLobbyEmitterForPlayer(any(UUID.class), any(UUID.class))).thenReturn(sseEmitter);
        var urlTemplate = String.format("/lobby/%s/join", gameLobby.getId());

        assertEquals(1, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate).param("playerName", "Foo Bar"))
                .andExpect(status().isOk());
        assertEquals(2, gameLobby.getNumPlayers());
    }

    @Test
    void shouldRemovePlayer() throws Exception {
        var owner = new Player("Owner");
        var gameLobby = new GameLobby("Test", owner);
        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);

        var urlTemplate = String.format("/lobby/%s/leave", gameLobby.getId());

        assertEquals(1, gameLobby.getNumPlayers());
        mvc.perform(MockMvcRequestBuilders.delete(urlTemplate).param("playerId", owner.getId().toString()))
                .andExpect(status().isOk());
        assertEquals(0, gameLobby.getNumPlayers());
    }

    @Test
    void shouldStartAGame() throws Exception {
        var owner = new Player("Owner");
        var gameLobby = new GameLobby("Test", owner);
        for (int i = 0; i < GameLobby.MIN_PLAYERS; i++) {
            gameLobby.addPlayer(new Player("" + i));
        }

        var emitter = new SseEmitter();
        var lobbySettings = gameLobby.getLobbySettings();
        lobbySettings.setGameMode(GameMode.DEBUG);
        var game = new Game(gameLobby.getId(), GameSettings.from(lobbySettings), Set.of(new Player("foo")));
        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);
        Mockito.when(server.startGame(any(UUID.class))).thenReturn(game);
        Mockito.when(emitterService.getLobbyEmitterForPlayer(any(UUID.class), any(UUID.class))).thenReturn(emitter);

        var urlTemplate = String.format("/lobby/%s/start", gameLobby.getId());

        assertEquals(0, server.getActiveGames().size());
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(game.getId().toString())));
        verify(server, times(1)).startGame(gameLobby.getId());
    }

    @Test
    void shouldBeAbleToSubscribeToSseEmitter() throws Exception {
        var owner = new Player("Owner");
        var gameLobby = new GameLobby("Test", owner);
        var player = new Player("P1");

        this.emitterService.createPlayerEmitterForLobby(gameLobby.getId(), owner.getId());

        Mockito.when(server.createGameLobby(any(String.class), any(Player.class))).thenReturn(gameLobby);
        Mockito.when(server.gameLobbyById(any(UUID.class))).thenReturn(gameLobby);

        var urlTemplate = String.format("/lobby/%s/subscribe/%s", gameLobby.getId(), player.getId());

        mvc.perform(MockMvcRequestBuilders.get(urlTemplate).contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());
    }
}