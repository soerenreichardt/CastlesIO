package io.castles.core.controller;

import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/lobby/{lobby_id}")
public class LobbyController {

    @Autowired
    Server server;

    @PutMapping("/join")
    void addPlayer(@RequestParam("lobby_id") UUID id, @RequestBody Player player) {
        GameLobby gameLobby = server.gameLobbyById(id);
        gameLobby.addPlayer(player);
    }
}
