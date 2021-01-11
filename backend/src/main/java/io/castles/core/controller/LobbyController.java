package io.castles.core.controller;

import io.castles.core.service.GameService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/lobby/{id}")
public class LobbyController {

    private final Server server;
    private final GameService gameService;
    private final SseEmitterService emitterService;

    public LobbyController(Server server, GameService gameService, SseEmitterService emitterService) {
        this.server = server;
        this.gameService = gameService;
        this.emitterService = emitterService;
    }

    @PutMapping("/join")
    UUID addPlayer(@PathVariable("id") UUID id, @RequestParam String playerName) throws IOException {
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
        return gameService.createGame(id).getId();
    }

    @GetMapping("/subscribe")
    SseEmitter subscribe(@PathVariable("id") UUID id) throws IOException {
        var sseEmitter = this.emitterService.getEmitterById(id);
        sseEmitter.send(String.format("Successfully subscribed to emitter %s", id));
        return sseEmitter;
    }
}
