package io.castles.core.controller;

import io.castles.core.model.PlayerDTO;
import io.castles.game.GameLobby;
import io.castles.game.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/lobby/{id}")
public class LobbyController {

    @Autowired
    Server server;

    @PutMapping("/join")
    void addPlayer(@PathVariable("id") UUID id, @RequestBody PlayerDTO player) {
        GameLobby gameLobby = server.gameLobbyById(id);
        gameLobby.addPlayer(player.toPlayer()); // TODO: exception handling
    }

    @DeleteMapping("/leave")
    void removePlayer(@PathVariable("id") UUID id, @RequestBody PlayerDTO player) {
        GameLobby gameLobby = server.gameLobbyById(id);
        gameLobby.removePlayer(player.toPlayer()); // TODO: exception handling
    }

    @PostMapping("/start")
    UUID startGame(@PathVariable("id") UUID id) {
        return server.startGame(id).getId();
    }

}
