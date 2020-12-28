package io.castles.game;

import io.castles.core.Board;

import java.util.*;

public class Game extends IdentifiableObject {

    private final List<Player> players;
    private Player activePlayer;

    private final GameLogic gameLogic;

    private final Board board;
    private GameSettings settings;

    public Game(GameSettings settings, Set<Player> players) {
        this.settings = settings;
        // Transforming the players set into a list might be
        // necessary if a player leaves the game while the
        // game is running. A set might trigger a rehash and
        // shuffle the order of players.
        this.players = new LinkedList<>(players);
        this.gameLogic = new GameLogic(settings.gameMode());
        this.board = Board.create(settings.gameMode());
        this.activePlayer = chooseRandomStartPlayer();
    }

    public GameState getCurrentGameState() {
        return this.gameLogic.getGameState();
    }

    private Player chooseRandomStartPlayer() {
        Random rand = new Random();
        return players.get(rand.nextInt(players.size()));
    }
}
