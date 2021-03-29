package io.castles.core.service;

import io.castles.core.events.ServerEventConsumer;
import io.castles.core.model.LobbyStateDTO;
import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.events.GameEventConsumer;

import java.util.UUID;

class EmittingEventConsumer implements ServerEventConsumer, GameEventConsumer {

    private final GameLobby gameLobby;
    private final PlayerEmitters playerEmitters;

    public EmittingEventConsumer(GameLobby gameLobby, PlayerEmitters playerEmitters) {
        this.gameLobby = gameLobby;
        this.playerEmitters = playerEmitters;
    }

    public void onPlayerReconnected(UUID playerId) {
        sendLobbyStateToAllPlayers();
    }

    @Override
    public void onPlayerAdded(Player player) {
        createPlayerEmitter(player);
        sendLobbyStateToAllPlayers();
    }

    @Override
    public void onPlayerRemoved(Player player) {
        playerEmitters.remove(player.getId());
        sendToAllPlayers(LobbyStateDTO.from(gameLobby));
    }

    @Override
    public void onSettingsChanged(GameLobbySettings gameLobbySettings) {
        sendLobbyStateToAllPlayers();
    }

    private void createPlayerEmitter(Player player) {
        playerEmitters.create(player.getId());
    }

    private void sendLobbyStateToAllPlayers() {
        var lobbyStateDTO = LobbyStateDTO.from(gameLobby);
        var ownerLobbyStateDTO = LobbyStateDTO.from(gameLobby);
        ownerLobbyStateDTO.getLobbySettings().setEditable(true);
        gameLobby.getPlayers().forEach(p -> {
            if (gameLobby.getOwnerId().equals(p.getId())) {
                playerEmitters.sendToPlayer(p, ownerLobbyStateDTO);
            } else {
                playerEmitters.sendToPlayer(p, lobbyStateDTO);
            }
        });
    }

    private void sendToAllPlayers(Object message) {
        gameLobby.getPlayers().forEach(player -> playerEmitters.sendToPlayer(player, message));
    }
}
