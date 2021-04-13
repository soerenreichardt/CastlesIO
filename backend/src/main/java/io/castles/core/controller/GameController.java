package io.castles.core.controller;

import io.castles.core.exceptions.UnableToReconnectException;
import io.castles.core.model.dto.GameDTO;
import io.castles.core.model.dto.GameStateDTO;
import io.castles.core.model.dto.PlayerDTO;
import io.castles.core.model.dto.TileDTO;
import io.castles.core.service.GameService;
import io.castles.core.tile.Tile;
import io.castles.game.Game;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/game/{id}")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/new_tile")
    @ResponseBody
    TileDTO getNextTile(@PathVariable("id") UUID id, @RequestParam("playerId") UUID playerId) {
        Tile newTile = gameService.getNewTile(id, playerId);
        return TileDTO.from(newTile);
    }

    @GetMapping(value = "/tile")
    @ResponseBody
    TileDTO getTile(@PathVariable("id") UUID id, @RequestParam("x") int x, @RequestParam("y") int y) {
        Game game = gameService.gameById(id);
        Tile tile = game.getTile(x, y);
        return TileDTO.from(tile);
    }

    @GetMapping(value = "/state")
    @ResponseBody
    GameStateDTO getGameState(@PathVariable("id") UUID id) {
        Game game = gameService.gameById(id);
        return new GameStateDTO(game.getCurrentGameState(), PlayerDTO.from(game.getActivePlayer()));
    }

    @GetMapping(value = "/")
    GameDTO getGameDTO(@PathVariable("id") UUID id, @RequestParam("playerId") UUID playerId) {
        Game game = gameService.gameById(id);
        if (!game.containsPlayer(playerId)) {
            throw new RuntimeException("You are no player of this game");
        }
        return GameDTO.from(game);
    }

    @PostMapping(value = "/tile")
    void insertTile(@PathVariable("id") UUID id, @RequestParam("playerId") UUID playerId, @RequestParam("x") int x, @RequestParam("y") int y, @RequestBody TileDTO tile) {
        gameService.placeTile(id, playerId, x, y, tile);
    }

    @PostMapping(value = "/restart")
    void restartGame(@PathVariable("id") UUID id) {
        gameService.gameById(id).restart();
    }

    @GetMapping("/subscribe/{playerId}")
    SseEmitter subscribe(@PathVariable("id") UUID id, @PathVariable("playerId") UUID playerId) {
        try {
            return gameService.reconnectToGame(id, playerId);
        } catch (UnableToReconnectException e) {
            throw new RuntimeException(e);
        }
    }
}
