package io.castles.core.controller;

import io.castles.core.model.LobbySettingsDTO;
import io.castles.core.model.LobbyStateDTO;
import io.castles.core.model.PlayerIdentificationDTO;
import io.castles.core.service.LobbyService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ServerController {

    private final Server server;
    private final LobbyService lobbyService;
    private final SseEmitterService emitterService;

    @GetMapping("/status")
    @ResponseBody
    HttpStatus getStatus() {
        return HttpStatus.OK;
    }

    @GetMapping("/settings")
    @ResponseBody
    LobbySettingsDTO getDefaultLobbySettings() {
        var defaultSettings = GameLobbySettings.builder().build();
        return LobbySettingsDTO.from(defaultSettings);
    }

    @PostMapping("/lobby")
    @ResponseBody
    PlayerIdentificationDTO createLobby(@RequestParam("lobbyName") String name,
                                        @RequestParam("playerName") String playerName,
                                        @RequestBody() LobbySettingsDTO settings) {
        var player = new Player(playerName);
        var gameLobby = this.server.createGameLobby(name, player);
        gameLobby.initializeWith(emitterService.eventConsumerFor(gameLobby));
        gameLobby.changeSettings(settings.toGameLobbySettings());

        return new PlayerIdentificationDTO(gameLobby.getId(), player.getId());
    }

    @GetMapping("/lobbies")
    @ResponseBody
    List<LobbyStateDTO> listPublicLobbies() {
        return this.server.publicGameLobbies().stream().map(LobbyStateDTO::from).collect(Collectors.toList());
    }
}
