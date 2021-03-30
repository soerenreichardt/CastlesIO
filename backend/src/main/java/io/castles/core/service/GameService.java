package io.castles.core.service;

import io.castles.core.model.dto.GameStartDTO;
import io.castles.core.model.dto.TileDTO;
import io.castles.core.tile.Tile;
import io.castles.core.util.JsonTileLoader;
import io.castles.game.Game;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class GameService {

    private final Server server;
    private final SseEmitterService emitterService;

    public GameService(Server server, SseEmitterService emitterService) {
        this.server = server;
        this.emitterService = emitterService;
    }

    public Game gameById(UUID id) {
        return this.server.gameById(id);
    }

    public Game createGame(UUID id) throws IOException {
        setDefaultTileList(id);

        Game game = this.server.startGame(id);
        UUID gameId = game.getId();
        GameStartDTO gameStartDTO = new GameStartDTO(
                id,
                game.getPlayers(),
                game.getActivePlayer(),
                game.getSettings(),
                TileDTO.from(game.getTile(0, 0))
        );
        for (Player player : game.getPlayers()) {
            // TODO: remove when implementing game start event
            this.emitterService.getLobbyEmitterForPlayer(gameId, player.getId()).send(gameStartDTO, MediaType.APPLICATION_JSON);
        }
        return game;
    }

    public Tile getNewTile(UUID gameId) {
        return gameById(gameId).getNewTile();
    }

    public void placeTile(UUID gameId, int x, int y, TileDTO tileDTO) {
        gameById(gameId).placeTile(tileDTO.toTile(), x, y);
    }

    private void setDefaultTileList(UUID id) throws IOException {
        var gameLobby = server.gameLobbyById(id);
        // TODO: the tile list should be configurable in future
        gameLobby.setTileList(new JsonTileLoader().getTilesFromResource());
    }
}
