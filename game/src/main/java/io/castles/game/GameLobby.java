package io.castles.game;

import io.castles.core.GameMode;
import io.castles.game.GameSettings.GameSettingsBuilder;

import java.util.HashSet;
import java.util.Set;

public class GameLobby extends IdentifiableObject {

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 5;

    private final Set<Player> players;
    private final GameSettingsBuilder settingsBuilder;
    private final String name;

    public GameLobby(String name) {
        this.name = name;
        this.players = new HashSet<>();
        this.settingsBuilder = GameSettings.builder();
        this.settingsBuilder.name(name);
        this.settingsBuilder.gameMode(GameMode.RANDOM);
    }

    Game startGame() {
        if (!canStart()) {
            throw new IllegalStateException("Unable to start game");
        }
        return new Game(this.settingsBuilder.build(), this.players);
    }

    public void addPlayer(Player player) {
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalArgumentException("Maximum number of players reached for this game.");
        }
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        boolean removed = this.players.remove(player);
        if (!removed) {
            throw new IllegalArgumentException(String.format("Player %s was not found in the list of players %s", player, players));
        }
    }

    public int getNumPlayers() {
        return players.size();
    }

    public void setGameMode(GameMode gameMode) {
        this.settingsBuilder.gameMode(gameMode);
    }

    public boolean canStart() {
        return players.size() >= MIN_PLAYERS && players.size() <= MAX_PLAYERS;
    }

    public String getName() {
        return this.name;
    }
}
