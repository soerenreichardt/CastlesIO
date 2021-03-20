package io.castles.game;

import io.castles.core.GameMode;
import io.castles.core.Visibility;
import io.castles.core.tile.Tile;
import io.castles.game.events.Event;
import io.castles.game.events.StatefulObject;

import java.util.*;
import java.util.stream.Collectors;

public class GameLobby extends StatefulObject {

    public static final int MIN_PLAYERS = 2;

    private final Set<Player> players;
    private final String name;
    private Player owner;
    private GameLobbySettings lobbySettings;

    public GameLobby(String name, Player owner) {
        this.name = name;
        this.players = new HashSet<>();
        this.owner = owner;
        this.lobbySettings = GameLobbySettings.builder().build();
        addPlayer(owner);
    }

    @Override
    public void initializeWith(EventConsumer eventConsumer) {
        registerCallback(eventConsumer);
        triggerEvent(Event.PLAYER_ADDED, owner);
    }

    boolean isPublic() {
        return this.lobbySettings.getVisibility() == Visibility.PUBLIC;
    }

    Game startGame() {
        if (!canStart()) {
            throw new IllegalStateException("Unable to start game");
        }
        return new Game(getId(), GameSettings.from(lobbySettings), this.players);
    }

    public void addPlayer(Player player) {
        if (players.size() >= lobbySettings.getMaxPlayers()) {
            throw new IllegalArgumentException("Maximum number of players reached for this game.");
        }
        this.players.add(player);
        triggerEvent(Event.PLAYER_ADDED, player);
    }

    public void removePlayer(Player player) {
        boolean removed = this.players.remove(player);
        if (!removed) {
            throw new IllegalArgumentException(String.format("Player %s was not found in the list of players %s", player, players));
        }
        if (player.getId() == owner.getId()) {
            replaceOwner();
        }
        triggerEvent(Event.PLAYER_REMOVED, player);
    }

    public void removePlayer(UUID playerId) {
        removePlayer(getPlayerById(playerId));
    }

    public Player getPlayerById(UUID playerId) {
        return this.players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("Player with if %s was not found in the list of players %s", playerId, players)));
    }

    private void replaceOwner() {
        if (!this.players.isEmpty()) {
            this.owner = this.players.iterator().next();
        }
    }

    public boolean containsPlayer(UUID playerId) {
        var playerIds = this.players.stream().map(Player::getId).collect(Collectors.toList());
        return playerIds.contains(playerId);
    }

    public int getNumPlayers() {
        return players.size();
    }

    public void changeSettings(GameLobbySettings gameLobbySettings) {
        this.lobbySettings = gameLobbySettings;
        triggerEvent(Event.SETTINGS_CHANGED, gameLobbySettings);
    }

    public void setGameMode(GameMode gameMode) {
        this.lobbySettings.setGameMode(gameMode);
    }

    public void setTileList(List<Tile> tiles) {
        this.lobbySettings.setTileList(tiles);
    }

    public boolean canStart() {
        return players.size() >= MIN_PLAYERS && players.size() <= lobbySettings.getMaxPlayers();
    }

    public String getName() {
        return this.name;
    }

    public int getMaxPlayers() {
        return lobbySettings.getMaxPlayers();
    }

    public List<String> getPlayerNames() {
        return this.players.stream().map(Player::getName).collect(Collectors.toList());
    }

    public UUID getOwnerId() {
        return this.owner.getId();
    }

    public GameLobbySettings getLobbySettings() {
        return this.lobbySettings;
    }

    public List<UUID> getPlayerIds() {
        return this.players.stream().map(Player::getId).collect(Collectors.toList());
    }

    public Collection<Player> getPlayers() {
        return this.players;
    }
}
