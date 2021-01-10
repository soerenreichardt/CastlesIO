package io.castles.core.controller;

import io.castles.game.GameLobby;
import io.castles.game.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class ServerController {

    @Autowired
    Server server;

    @GetMapping("/status")
    @ResponseBody
    HttpStatus getStatus() {
        return HttpStatus.OK;
    }

    @PostMapping("/lobby")
    @ResponseBody
    UUID createLobby(@RequestBody String name) {
        GameLobby gameLobby = server.createGameLobby(name);
        return gameLobby.getId();
    }
}
