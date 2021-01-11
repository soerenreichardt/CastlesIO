package io.castles.core.controller;

import io.castles.core.service.SseEmitterService;
import io.castles.game.Server;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class ServerController {

    private final Server server;
    private final SseEmitterService emitterService;

    public ServerController(Server server, SseEmitterService emitterService) {
        this.server = server;
        this.emitterService = emitterService;
    }

    @GetMapping("/status")
    @ResponseBody
    HttpStatus getStatus() {
        return HttpStatus.OK;
    }

    @PostMapping("/lobby")
    @ResponseBody
    UUID createLobby(@RequestParam("lobbyName") String name) {
        var gameLobbyId = this.server.createGameLobby(name).getId();
        this.emitterService.createEmitter(gameLobbyId);
        return gameLobbyId;
    }
}
