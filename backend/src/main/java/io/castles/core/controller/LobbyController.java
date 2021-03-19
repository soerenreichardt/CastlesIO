package io.castles.core.controller;

import io.castles.core.model.LobbySettingsDTO;
import io.castles.core.model.LobbyStateDTO;
import io.castles.core.model.PublicLobbyDTO;
import io.castles.core.service.GameService;
import io.castles.core.service.LobbyService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.GameLobby;
import io.castles.game.Player;
import io.castles.game.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    UUID addPlayer(@PathVariable("id") UUID id, @RequestParam("playerName") String playerName) {
        var player = new Player(playerName);
        try {
            lobbyService.joinLobby(id, player);
        } catch (IOException e) {
            // TODO: exception handling
            throw new RuntimeException(e);
        }
        return player.getId();
    }

    @GetMapping("/info")
    PublicLobbyDTO getPublicLobbyInfo(@PathVariable("id") UUID id) {
        GameLobby gameLobby = server.gameLobbyById(id);
        return PublicLobbyDTO.from(gameLobby);
    }

    @PostMapping("/update")
    HttpStatus updateLobbySettings(@PathVariable("id") UUID id,
                                   @RequestParam() UUID playerId,
                                   @RequestBody LobbySettingsDTO settings) {
        GameLobby gameLobby = server.gameLobbyById(id);
        if (gameLobby.getOwnerId().equals(playerId)) {
            this.lobbyService.updateLobbySettings(gameLobby, settings);
            return HttpStatus.OK;
        }
        return HttpStatus.FORBIDDEN;
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

    @GetMapping("/status/{playerId}")
    @ResponseBody
    LobbyStateDTO getLobbyState(@PathVariable("id") UUID id, @PathVariable UUID playerId) {
        GameLobby gameLobby = server.gameLobbyById(id);
        if (gameLobby.containsPlayer(playerId)) {
            return this.lobbyService.getLobbyStateDTOFromGameLobbyForPlayer(gameLobby, playerId);
        } else {
            throw new RuntimeException("You are no player of this lobby");
        }
    }

    @GetMapping("/subscribe/{playerId}")
    SseEmitter subscribe(@PathVariable("id") UUID id, @PathVariable("playerId") UUID playerId) {
        var playerEmitter = this.emitterService.getLobbyEmitterForPlayer(id, playerId);
        if (playerEmitter == null) {
            playerEmitter = this.lobbyService.reconnectLobby(id, playerId);
        }
        lobbyService.updateLobbyState(id);
        return playerEmitter;
    }
}
