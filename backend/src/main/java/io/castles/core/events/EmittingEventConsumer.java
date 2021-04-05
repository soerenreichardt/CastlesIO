package io.castles.core.events;

import io.castles.core.model.dto.*;
import io.castles.core.service.ClockService;
import io.castles.core.service.PlayerEmitters;
import io.castles.game.*;
import io.castles.game.events.GameEvent;
import io.castles.game.events.GameEventConsumer;

public class EmittingEventConsumer implements ServerEventConsumer, GameEventConsumer {

    private final GameLobby gameLobby;
    private final PlayerEmitters playerEmitters;
    private final ConnectionHandler connectionHandler;

    public EmittingEventConsumer(GameLobby gameLobby, PlayerEmitters playerEmitters, ClockService clockService) {
        this.gameLobby = gameLobby;
        this.playerEmitters = playerEmitters;
        this.connectionHandler = new ConnectionHandler(playerEmitters, clockService);
    }

    @Override
    public void onPlayerReconnectAttempt(Player player) {
        connectionHandler.checkDisconnectionTimeout();
        connectionHandler.tryReconnectPlayer(player);
    }

    @Override
    public void onPlayerReconnected(Player player) {
        sendToAllPlayers(new EventMessageDTO<>(ServerEvent.PLAYER_DISCONNECTED.name(), PlayerDTO.from(player)));
    }

    @Override
    public void onPlayerDisconnected(Player player) {
        connectionHandler.playerDisconnected(player);
        sendToAllPlayers(new EventMessageDTO<>(ServerEvent.PLAYER_RECONNECTED.name(), PlayerDTO.from(player)));
    }

    @Override
    public void onPlayerTimeout(Player player) {
        sendToAllPlayers(new EventMessageDTO<>(ServerEvent.PLAYER_TIMEOUT.name(), PlayerDTO.from(player)));
    }

    @Override
    public void onPlayerAdded(Player player) {
        createPlayerEmitter(player);
        sendToAllPlayers(new EventMessageDTO<>(GameEvent.PLAYER_ADDED.name(), PlayerDTO.from(player)));
    }

    @Override
    public void onPlayerRemoved(Player player) {
        playerEmitters.remove(player.getId());
        sendToAllPlayers(new EventMessageDTO<>(GameEvent.PLAYER_REMOVED.name(), PlayerDTO.from(player)));
    }

    @Override
    public void onSettingsChanged(GameLobbySettings gameLobbySettings) {
        sendToAllPlayers(new EventMessageDTO<>(GameEvent.SETTINGS_CHANGED.name(), LobbySettingsDTO.from(gameLobbySettings)));
    }

    @Override
    public void onGameStarted(Game game) {
        sendToAllPlayers(new EventMessageDTO<>(GameEvent.GAME_STARTED.name(), GameStartDTO.from(game)));
    }

    @Override
    public void onActivePlayerSwitched(Player activePlayer) {
        sendToAllPlayers(new EventMessageDTO<>(GameEvent.ACTIVE_PLAYER_SWITCHED.name(), PlayerDTO.from(activePlayer)));
    }

    @Override
    public void onPhaseSwitched(GameState from, GameState to) {
        sendToAllPlayers(new EventMessageDTO<>(GameEvent.PHASE_SWITCHED.name(), new PhaseSwitchDTO(from, to)));
    }

    private void createPlayerEmitter(Player player) {
        playerEmitters.create(player.getId());
    }

    private void sendToAllPlayers(Object message) {
        connectionHandler.checkDisconnectionTimeout();
        var disconnectedPlayers = connectionHandler.disconnectedPlayers();
        gameLobby.getPlayers().forEach(player -> {
            if (!disconnectedPlayers.contains(player)) {
                playerEmitters.sendToPlayer(player, message);
            }
        });
    }
}
