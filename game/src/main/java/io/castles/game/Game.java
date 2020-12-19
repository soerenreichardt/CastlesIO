package io.castles.game;

import io.castles.core.Board;

import java.util.List;
import java.util.Random;

public class Game extends IdentifiableObject {

    private final List<Player> players;
    private Player activePlayer;

    private final GameLogic gameLogic;

    private final Board board;
    private GameSettings settings;

    public Game(GameSettings settings, List<Player> players) {
        this.settings = settings;
        this.players = players;
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
