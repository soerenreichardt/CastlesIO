package io.castles.game;

import io.castles.core.board.Board;
import io.castles.core.tile.Tile;
import io.castles.game.events.StatefulObject;

import java.util.*;

public class Game extends StatefulObject {

    private final GameLogic gameLogic;

    private final Board board;
    private final GameSettings settings;

    public Game(UUID lobbyId, GameSettings settings, Set<Player> players) {
        super(lobbyId);
        this.settings = settings;
        // Transforming the players set into a list might be
        // necessary if a player leaves the game while the
        // game is running. A set might trigger a rehash and
        // shuffle the order of players.
        this.gameLogic = new GameLogic(settings.getGameMode(), new LinkedList<>(players));
        this.board = Board.create(settings.getGameMode(), settings.getTileList());
    }

    @Override
    public void initializeWith(EventConsumer eventConsumer) {
        throw new UnsupportedOperationException();
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

    public Player getActivePlayer() {
        return this.gameLogic.getActivePlayer();
    }

    public List<Player> getPlayers() {
        return this.gameLogic.getPlayers();
    }

    public GameSettings getSettings() {
        return this.settings;
    }

    public void placeTile(Tile tile, int x, int y) {
        validateAction(GameState.PLACE_TILE);
        this.board.insertTileToBoard(tile, x, y);
        this.gameLogic.nextPhase();
    }

    private void validateAction(GameState expectedState) throws IllegalStateException {
        if (expectedState != getCurrentGameState()) {
            throw new IllegalStateException(String.format("Expected GameState to be %s, but was %s", expectedState, getCurrentGameState()));
        }
    }
}
