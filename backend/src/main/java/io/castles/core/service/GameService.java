package io.castles.core.service;

import io.castles.core.exceptions.UnableToReconnectException;
import io.castles.core.model.dto.TileDTO;
import io.castles.core.tile.Tile;
import io.castles.core.util.JsonTileLoader;
import io.castles.exceptions.GrasRegionOccupiedException;
import io.castles.exceptions.NoFiguresLeftException;
import io.castles.game.Game;
import io.castles.game.Server;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
        return this.server.startGame(id);
    }

    public Tile getNewTile(UUID gameId, UUID playerId) {
        var game = gameById(gameId);
        var player = game.getPlayerById(playerId);
        return game.drawTile(player);
    }

    public Tile getDrawnTile(UUID gameId, UUID playerId) {
        var game = gameById(gameId);
        var player = game.getPlayerById(playerId);
        return game.getDrawnTile(player);
    }

    public void placeTile(UUID gameId, UUID playerId, int x, int y, TileDTO tileDTO) {
        var game = gameById(gameId);
        var player = game.getPlayerById(playerId);
        game.placeTile(player, tileDTO.toTile(), x, y);
    }

    public void skipPhase(UUID gameId, UUID playerId) {
        var game = gameById(gameId);
        var player = game.getPlayerById(playerId);
        game.skipPhase(player);
    }

    public void placeFigure(UUID gameId, UUID playerId, int x, int y, int row, int column) throws GrasRegionOccupiedException, NoFiguresLeftException {
        var game = gameById(gameId);
        var player = game.getPlayerById(playerId);
        game.placeFigure(player, game.getTile(x, y), row, column);
    }

    public SseEmitter reconnectToGame(UUID id, UUID playerId) throws UnableToReconnectException {
        return emitterService.reconnectPlayer(gameById(id), playerId);
    }

    private void setDefaultTileList(UUID id) throws IOException {
        var gameLobby = server.gameLobbyById(id);
        // TODO: the tile list should be configurable in future
        gameLobby.setTileList(new JsonTileLoader().getTilesFromResource());
    }
}
