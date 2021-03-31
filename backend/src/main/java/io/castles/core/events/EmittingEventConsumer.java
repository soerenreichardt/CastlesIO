package io.castles.core.events;

import io.castles.core.model.dto.EventMessageDTO;
import io.castles.core.model.dto.LobbySettingsDTO;
import io.castles.core.model.dto.LobbyStateDTO;
import io.castles.core.model.dto.PlayerDTO;
import io.castles.core.service.PlayerEmitters;
import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.events.GameEvent;
import io.castles.game.events.GameEventConsumer;

public class EmittingEventConsumer implements ServerEventConsumer, GameEventConsumer {

    private final GameLobby gameLobby;
    private final PlayerEmitters playerEmitters;

    public EmittingEventConsumer(GameLobby gameLobby, PlayerEmitters playerEmitters) {
        this.gameLobby = gameLobby;
        this.playerEmitters = playerEmitters;
    }

    @Override
    public void onPlayerReconnected(Player player) {
        sendToAllPlayers(new EventMessageDTO<>(ServerEvent.PLAYER_DISCONNECTED.name(), PlayerDTO.from(player)));
    }

    @Override
    public void onPlayerDisconnected(Player player) {
        sendToAllPlayers(new EventMessageDTO<>(ServerEvent.PLAYER_RECONNECTED.name(), PlayerDTO.from(player)));
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
    public void onLobbyCreated(GameLobby gameLobby) {
        sendToAllPlayers(new EventMessageDTO<>(GameEvent.LOBBY_CREATED.name(), LobbyStateDTO.from(gameLobby)));
    }

    private void createPlayerEmitter(Player player) {
        playerEmitters.create(player.getId());
    }

    private void sendToAllPlayers(Object message) {
        gameLobby.getPlayers().forEach(player -> playerEmitters.sendToPlayer(player, message));
    }
}
