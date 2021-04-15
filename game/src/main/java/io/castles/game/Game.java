package io.castles.game;

import io.castles.core.board.Board;
import io.castles.core.tile.Tile;
import io.castles.game.events.EventHandler;
import io.castles.game.events.GameEvent;
import io.castles.game.events.StatefulObject;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Game extends StatefulObject implements PlayerContainer {

    private final GameLogic gameLogic;

    private final Board board;
    private final GameSettings settings;

    private Tile drawnTile;

    public Game(UUID lobbyId, GameSettings settings, Set<Player> players, EventHandler eventHandler) {
        super(lobbyId, eventHandler);
        this.settings = settings;
        // Transforming the players set into a list might be
        // necessary if a player leaves the game while the
        // game is running. A set might trigger a rehash and
        // shuffle the order of players.
        this.gameLogic = new GameLogic(getId(), settings.getGameMode(), new LinkedList<>(players), eventHandler);
        this.board = Board.create(settings.getGameMode(), settings.getTileList());
    }

    @Override
    protected void init() {
        triggerLocalEvent(getId(), GameEvent.GAME_STARTED, this);
        gameLogic.initialize();
    }

    @Override
    public void restart() {
        gameLogic.restart();
        board.restart();
    }

    @Override
    public List<Player> getPlayers() {
        return this.gameLogic.getPlayers();
    }

    @Override
    public Player getPlayerById(UUID playerId) {
        var players = this.gameLogic.getPlayers();
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("Player with if %s was not found in the list of players %s", playerId, players)));
    }

    @Override
    public boolean containsPlayer(UUID playerId) {
        var playerIds = this.getPlayers().stream().map(Player::getId).collect(Collectors.toList());
        return playerIds.contains(playerId);
    }

    public Tile getStartTile() {
        return this.board.getTile(0, 0);
    }

    public Tile getTile(int x, int y) {
        return this.board.getTile(x, y);
    }

    public Tile getDrawnTile(Player player) {
        this.validateAction(player, GameState.PLACE_TILE);
        return this.drawnTile;
    }

    public Map<Integer, Map<Integer, Tile>> getGameBoardTileMap() {
        return this.board.getTiles();
    }

    public GameState getCurrentGameState() {
        return this.gameLogic.getGameState();
    }

    public Player getActivePlayer() {
        return this.gameLogic.getActivePlayer();
    }

    public GameSettings getSettings() {
        return this.settings;
    }

    @TestOnly
    void setGameState(GameState gameState) {
        while(gameLogic.getGameState() != gameState) {
            gameLogic.nextPhase();
        }
    }

    // ========== ACTIONS =========

    public Tile drawTile(Player player) {
        drawnTile = gameAction(player, GameState.DRAW, board::getNewTile);
        return drawnTile;
    }

    public void placeTile(Player player, Tile tile, int x, int y) {
        gameAction(player, GameState.PLACE_TILE, () -> {
            this.board.insertTileToBoard(tile, x, y);
            triggerLocalEvent(getId(), GameEvent.TILE_PLACED, tile, x, y);
        });
        drawnTile = null;
    }

    public void placeMeeple(Player player, Tile tile, int row, int column) {
        gameAction(player, GameState.PLACE_FIGURE, () -> {
            // call board to place meeple
            // trigger meeple placed
        });
    }

    public void skipPhase(Player player) {
        var gameState = getCurrentGameState();
        gameAction(player, gameState, gameLogic::skipPhase);
    }

    private void gameAction(Player player, GameState gameState, Runnable action) {
        validateAction(player, gameState);
        action.run();
        gameLogic.nextPhase();
    }

    private <T> T gameAction(Player player, GameState gameState, Supplier<T> action) {
        validateAction(player, gameState);
        T result = action.get();
        gameLogic.nextPhase();
        return result;
    }

    private void validateAction(Player player, GameState expectedState) throws IllegalStateException {
        if (player != getActivePlayer()) {
            throw new IllegalStateException(String.format("Player %s is not the active player %s", player, getActivePlayer()));
        }
        if (expectedState != getCurrentGameState()) {
            throw new IllegalStateException(String.format("Expected GameState to be %s, but was %s", expectedState, getCurrentGameState()));
        }
    }
}
