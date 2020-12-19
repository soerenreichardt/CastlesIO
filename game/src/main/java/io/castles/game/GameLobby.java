package io.castles.game;

import io.castles.core.GameMode;

import java.util.LinkedList;
import java.util.List;

public class GameLobby {

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 5;

    private final List<Player> players;
    private final ImmutableGameSettings.Builder settingsBuilder;

    public GameLobby() {
        this.players = new LinkedList<>();
        this.settingsBuilder = ImmutableGameSettings.builder();
    }

    public Game startGame() {
        if (!canStart()) {
            throw new IllegalStateException("Unable to start game");
        }
        return new Game(this.settingsBuilder.build(), this.players);
    }

    public void addPlayer(Player player) {
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalArgumentException("Maxmimum number of player reached for this game.");
        }
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        boolean removed = this.players.remove(player);
        if (!removed) {
            throw new IllegalArgumentException(String.format("Player %s was not found in the list of players %s", player, players));
        }
    }

    public void setGameMode(GameMode gameMode) {
        this.settingsBuilder.gameMode(gameMode);
    }

    private boolean canStart() {
        return players.size() >= MIN_PLAYERS && players.size() <= MAX_PLAYERS;
    }
}
