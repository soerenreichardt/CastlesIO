package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.util.JsonTileLoader;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/lobby/{id}")
public class LobbyController {

    @Autowired
    Server server;

    @PutMapping("/join")
    UUID addPlayer(@PathVariable("id") UUID id, @RequestParam String playerName) {
        GameLobby gameLobby = server.gameLobbyById(id);
        Player player = new Player(playerName);
        gameLobby.addPlayer(player); // TODO: exception handling
        return player.getId();
    }

    @DeleteMapping("/leave")
    void removePlayer(@PathVariable("id") UUID id, @RequestParam UUID playerId) {
        GameLobby gameLobby = server.gameLobbyById(id);
        gameLobby.removePlayer(playerId); // TODO: exception handling
    }

    @PostMapping("/start")
    UUID startGame(@PathVariable("id") UUID id) throws IOException {
        var gameLobby = server.gameLobbyById(id);
        // TODO: the tile list should be configurable in future
        gameLobby.setTileList(new JsonTileLoader().getTilesFromResource());
        return server.startGame(id).getId();
    }

}
