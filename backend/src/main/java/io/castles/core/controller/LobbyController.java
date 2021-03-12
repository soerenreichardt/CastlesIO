package io.castles.core.controller;

import io.castles.core.service.GameService;
import io.castles.core.service.LobbyService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/lobby/{id}")
@RequiredArgsConstructor
public class LobbyController {

    private final Server server;
    private final GameService gameService;
    private final SseEmitterService emitterService;
    private final LobbyService lobbyService;

    @PutMapping("/join")
    UUID addPlayer(@PathVariable("id") UUID id, @RequestParam String playerName) {
        var player = new Player(playerName);
        try {
            lobbyService.joinLobby(id, player);
        } catch (IOException e) {
            // TODO: exception handling
            throw new RuntimeException(e);
        }
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

    @GetMapping("/subscribe/{playerId}")
    SseEmitter subscribe(@PathVariable("id") UUID id, @PathVariable("playerId") UUID playerId) {
        var gameLobby = server.gameLobbyById(id);
        var sseEmitter = this.emitterService.getLobbyEmitterForPlayer(id, playerId);
        try {
            lobbyService.updateLobbyState(gameLobby);
        } catch (IOException e) {
            // TODO: exception handling
            throw new RuntimeException(e);
        }
        return sseEmitter;
    }
}
