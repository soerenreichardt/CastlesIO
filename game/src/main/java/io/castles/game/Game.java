package io.castles.game;

import io.castles.core.Board;
import io.castles.core.Tile;

import java.util.*;

public class Game extends IdentifiableObject {

    private final GameLogic gameLogic;

    private final Board board;
    private GameSettings settings;

    public Game(GameSettings settings, Set<Player> players) {
        this.settings = settings;
        // Transforming the players set into a list might be
        // necessary if a player leaves the game while the
        // game is running. A set might trigger a rehash and
        // shuffle the order of players.
        this.gameLogic = new GameLogic(settings.gameMode(), new LinkedList<>(players));
        this.board = Board.create(settings.gameMode());
    }

    public Tile getNewTile() {
        return this.board.getNewTile();
    }

    public Tile getTile(int x, int y) {
        return this.board.getTile(x, y);
    }

    public GameState getCurrentGameState() {
        return this.gameLogic.getGameState();
    }

    public void placeTile(Tile tile, int x, int y) {
        this.board.insertTileToBoard(tile, x, y);
        this.gameLogic.nextPhase();
    }
}
