package io.castles.core.service;

import io.castles.core.model.GameStartDTO;
import io.castles.core.util.JsonTileLoader;
import io.castles.game.Game;
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
                game.getTile(0, 0)
        );
        this.emitterService.getEmitterById(gameId).send(gameStartDTO, MediaType.APPLICATION_JSON);
        return game;
    }

    private void setDefaultTileList(UUID id) throws IOException {
        var gameLobby = server.gameLobbyById(id);
        // TODO: the tile list should be configurable in future
        gameLobby.setTileList(new JsonTileLoader().getTilesFromResource());
    }
}