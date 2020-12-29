package io.castles.core.controller;

import io.castles.core.Tile;
import io.castles.core.model.GameStateDTO;
import io.castles.core.model.TileDTO;
import io.castles.game.Game;
import io.castles.game.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game/{id}")
public class GameController {

    @Autowired
    Server server;

    @GetMapping("/new_tile")
    @ResponseBody
    Tile getNextTile(@PathVariable("id") UUID id) {
        Game game = server.gameById(id);
        return game.getNewTile();
    }

    @GetMapping(value = "/tile")
    @ResponseBody
    Tile getTile(@PathVariable("id") UUID id, @RequestParam("x") int x, @RequestParam("y") int y) {
        Game game = server.gameById(id);
        return game.getTile(x, y);
    }

    @GetMapping(value = "/state")
    @ResponseBody
    GameStateDTO getGameState(@PathVariable("id") UUID id) {
        Game game = server.gameById(id);
        return new GameStateDTO(game.getCurrentGameState(), game.getActivePlayer());
    }

    @PostMapping(value = "/tile")
    void insertTile(@PathVariable("id") UUID id, @RequestParam("x") int x, @RequestParam("y") int y, @RequestBody TileDTO tile) {
        Game game = server.gameById(id);
        game.placeTile(tile.toTile(), x, y);
    }
}
