package io.castles.core.controller;

import io.castles.core.model.LobbySettingsDTO;
import io.castles.core.model.PlayerIdentificationDTO;
import io.castles.core.model.PublicLobbyDTO;
import io.castles.core.service.LobbyService;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.core.model.LobbyStateDTO;
import io.castles.game.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ServerController {

    private final Server server;
    private final LobbyService lobbyService;

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
                                        @RequestBody() LobbySettingsDTO settings) throws IOException {
        Player player = new Player(playerName);
        var gameLobby = this.server.createGameLobby(name, player);
        lobbyService.joinLobby(gameLobby.getId(), player);
        lobbyService.updateLobbySettings(gameLobby, settings);

        return new PlayerIdentificationDTO(gameLobby.getId(), player.getId());
    }

    @GetMapping("/lobbies")
    @ResponseBody
    List<PublicLobbyDTO> listPublicLobbies() {
        return this.server.publicGameLobbies().stream().map(PublicLobbyDTO::from).collect(Collectors.toList());
    }
}
