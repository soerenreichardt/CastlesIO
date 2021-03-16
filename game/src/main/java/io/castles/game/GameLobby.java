package io.castles.game;

import io.castles.core.GameMode;
import io.castles.core.tile.Tile;
import java.util.*;
import java.util.stream.Collectors;

public class GameLobby extends IdentifiableObject {

    public static final int MIN_PLAYERS = 2;

    private final Set<Player> players;
    private final GameLobbySettings lobbySettings;
    private final String name;
    private Player owner;

    public GameLobby(String name) {
        this.name = name;
        this.players = new HashSet<>();
        this.owner = null;
        this.lobbySettings = GameLobbySettings.builder().build();
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
        if (this.owner == null) {
            this.owner = player;
        }
    }

    public void removePlayer(Player player) {
        boolean removed = this.players.remove(player);
        if (!removed) {
            throw new IllegalArgumentException(String.format("Player %s was not found in the list of players %s", player, players));
        }
        if (player.getId() == owner.getId()) {
            replaceOwner();
        }
    }

    public void removePlayer(UUID playerId) {
        removePlayer(getPlayerById(playerId));
    }

    private void replaceOwner() {
        if (!this.players.isEmpty()) {
            this.owner = this.players.iterator().next();
        }
    }

    private Player getPlayerById(UUID playerId) {
        return this.players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("Player with if %s was not found in the list of players %s", playerId, players)));
    }

    public boolean isPlayerInLobby(UUID playerId) {
        var playerIds = this.players.stream().map(Player::getId).collect(Collectors.toList());
        return playerIds.contains(playerId);
    }

    public int getNumPlayers() {
        return players.size();
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
}
