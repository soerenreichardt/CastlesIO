package io.castles.core.controller;

import io.castles.game.GameLobby;
import io.castles.game.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class ServerController {

    @Autowired
    Server server;

    @PostMapping("/lobby")
    @ResponseBody
    UUID createLobby(@RequestBody String name) {
        GameLobby gameLobby = server.createGameLobby(name);
        return gameLobby.getId();
    }
}
