package io.castles.core.controller;

import io.castles.core.model.PlayerIdentificationDTO;
import io.castles.game.Player;
import io.castles.game.Server;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class ServerController {

    private final Server server;

    public ServerController(Server server) {
        this.server = server;
    }

    @GetMapping("/status")
    @ResponseBody
    HttpStatus getStatus() {
        return HttpStatus.OK;
    }

    @PostMapping("/lobby")
    @ResponseBody
    PlayerIdentificationDTO createLobby(@RequestParam("lobbyName") String name, @RequestParam("playerName") String playerName) {
        Player player = new Player(playerName);
        var lobbyId = this.server.createGameLobby(name, player).getId();

        return new PlayerIdentificationDTO(lobbyId, player.getId());
    }
}
