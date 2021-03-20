package io.castles.core.service;

import io.castles.core.events.ServerEventConsumer;
import io.castles.core.model.LobbySettingsDTO;
import io.castles.core.model.LobbyStateDTO;
import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;

import java.util.UUID;

class EmittingEventConsumer implements ServerEventConsumer {

    private final GameLobby gameLobby;
    private final PlayerEmitters playerEmitters;

    public EmittingEventConsumer(GameLobby gameLobby, PlayerEmitters playerEmitters) {
        this.gameLobby = gameLobby;
        this.playerEmitters = playerEmitters;
    }

    public void onPlayerReconnected(UUID playerId) {
        sendToAllPlayers(String.format("Player %s connected", gameLobby.getPlayerById(playerId)));
    }

    @Override
    public void onPlayerAdded(Player player) {
        playerEmitters.create(player.getId());
        LobbyStateDTO lobbyStateDTO = LobbyStateDTO.from(gameLobby);
        if (gameLobby.getOwnerId().equals(player.getId())) {
            lobbyStateDTO.getLobbySettings().setEditable(true);
        }
        sendToAllPlayers(lobbyStateDTO);
    }

    @Override
    public void onPlayerRemoved(Player player) {
        playerEmitters.remove(player.getId());
        sendToAllPlayers(LobbyStateDTO.from(gameLobby));
    }

    @Override
    public void onSettingsChanged(GameLobbySettings gameLobbySettings) {
        sendToAllPlayers(LobbySettingsDTO.from(gameLobbySettings));
    }

    private void sendToAllPlayers(Object message) {
        gameLobby.getPlayers().forEach(player -> playerEmitters.sendToPlayer(player, message));
    }
}
