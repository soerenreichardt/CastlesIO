package io.castles.core.integration;

import io.castles.core.GameMode;
import io.castles.core.model.dto.LobbySettingsDTO;
import io.castles.core.model.dto.PlayerIdentificationDTO;
import io.castles.core.model.dto.TileDTO;
import io.castles.core.service.ClockService;
import io.castles.core.service.ServerEventService;
import io.castles.core.tile.Tile;
import io.castles.core.util.CollectingEventConsumer;
import io.castles.core.util.JsonHelper;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
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
public class CastlesIOIntegrationTest {

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

        var gameLobbySettings = GameLobbySettings.builder().gameMode(GameMode.DEBUG).build();
        changeSettings(lobbyId, server.gameLobbyById(lobbyId).getOwner().getId(), gameLobbySettings);

        var gameLobby = server.gameLobbyById(lobbyId);
        assertThat(gameLobby.getNumPlayers()).isEqualTo(3);
        assertThat(eventConsumer.events().keySet()).containsExactlyInAnyOrder(
                GameEvent.LOBBY_CREATED.name(),
                GameEvent.SETTINGS_CHANGED.name(),
                GameEvent.PLAYER_ADDED.name(),
                GameEvent.PLAYER_REMOVED.name()
        );

        startGame(lobbyId);

        var game = server.gameById(gameLobby.getId());
        var activePlayer = game.getActivePlayer();

        Tile startTile = getTile(lobbyId, 0, 0);
        assertThat(startTile).isEqualTo(game.getStartTile());

        Tile drawnTile = newTile(lobbyId, activePlayer);
        placeTile(lobbyId, activePlayer, drawnTile, 0, 1);
        assertThat(game.getTile(0, 1)).isEqualTo(drawnTile);

        placeMeeple(lobbyId, activePlayer, 0, 1, 0, 0);
        assertThat(game.getMeeples().size()).isEqualTo(1);

        assertThat(game.getActivePlayer()).isNotEqualTo(activePlayer);
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

    private void changeSettings(UUID lobbyId, UUID owner, GameLobbySettings lobbySettings) throws Exception {
        String changeSettingsUrl = String.format("/lobby/%s/update", lobbyId);
        var settingsJson = JsonHelper.serializeObject(LobbySettingsDTO.from(lobbySettings));
        mvc.perform(MockMvcRequestBuilders.post(changeSettingsUrl)
                .param("playerId", owner.toString())
                .content(settingsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void startGame(UUID lobbyId) throws Exception {
        String startGameUrl = String.format("/lobby/%s/start", lobbyId);
        mvc.perform(MockMvcRequestBuilders.post(startGameUrl))
                .andExpect(status().isOk());
    }

    private Tile getTile(UUID lobbyId, int x, int y) throws Exception {
        var newTileUrl = String.format("/game/%s/tile", lobbyId);
        var result = mvc.perform(MockMvcRequestBuilders.get(newTileUrl)
                .param("x", ((Integer) x).toString())
                .param("y", ((Integer) y).toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonHelper.deserializeObject(result, TileDTO.class).toTile();
    }

    private Tile newTile(UUID lobbyId, Player activePlayer) throws Exception {
        var newTileUrl = String.format("/game/%s/new_tile", lobbyId);
        var result = mvc.perform(MockMvcRequestBuilders.get(newTileUrl).param("playerId", activePlayer.getId().toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonHelper.deserializeObject(result, TileDTO.class).toTile();
    }

    private void placeTile(UUID lobbyId, Player activePlayer, Tile tile, int x, int y) throws Exception {
        var placeTileUrl = String.format("/game/%s/tile", lobbyId);
        var tileJson = JsonHelper.serializeObject(TileDTO.from(tile));
        mvc.perform(MockMvcRequestBuilders.post(placeTileUrl)
                .param("playerId", activePlayer.getId().toString())
                .param("x", Integer.toString(x))
                .param("y", Integer.toString(y))
                .content(tileJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void placeMeeple(UUID lobbyId, Player activePlayer, int x, int y, int row, int column) throws Exception {
        var placeMeepleUrl = String.format("/game/%s/meeple", lobbyId);
        mvc.perform(MockMvcRequestBuilders.post(placeMeepleUrl)
                .param("playerId", activePlayer.getId().toString())
                .param("x", Integer.toString(x))
                .param("y", Integer.toString(y))
                .param("row", Integer.toString(row))
                .param("column", Integer.toString(column)))
                .andExpect(status().isOk());
    }
}
