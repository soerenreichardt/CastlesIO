package io.castles.core.controller;

import io.castles.core.events.SetupEventConsumer;
import io.castles.core.model.dto.LobbySettingsDTO;
import io.castles.core.model.dto.LobbyStateDTO;
import io.castles.core.model.dto.PlayerIdentificationDTO;
import io.castles.core.service.EmittingEventConsumer;
import io.castles.core.service.ServerEventService;
import io.castles.core.service.SseEmitterService;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class ServerController {

    private final Server server;

    public ServerController(Server server, SseEmitterService emitterService, ServerEventService serverEventService) {
        this.server = server;

        this.server.eventHandler().registerEventConsumer(new SetupEventConsumer(serverEventService, emitterService));

        serverEventService.registerEventConsumerSupplier(id -> new EmittingEventConsumer(server.gameLobbyById(id), emitterService.getPlayerEmitters(id)));
    }

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
        gameLobby.changeSettings(settings.toGameLobbySettings());

        return new PlayerIdentificationDTO(gameLobby.getId(), player.getId());
    }

    @GetMapping("/lobbies")
    @ResponseBody
    List<LobbyStateDTO> listPublicLobbies() {
        return this.server.publicGameLobbies().stream().map(LobbyStateDTO::from).collect(Collectors.toList());
    }
}
