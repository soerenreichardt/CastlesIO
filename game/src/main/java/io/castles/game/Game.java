package io.castles.game;

import io.castles.core.board.Board;
import io.castles.core.graph.Graph;
import io.castles.core.tile.Figure;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.exceptions.NoFiguresLeftException;
import io.castles.exceptions.RegionOccupiedException;
import io.castles.game.events.EventHandler;
import io.castles.game.events.GameEvent;
import io.castles.game.events.StatefulObject;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Game extends StatefulObject implements PlayerContainer {

    public static final int FIGURES_PER_PLAYER = 7;

    private final String name;
    private final GameLogic gameLogic;
    private final Board board;
    private final GameSettings settings;
    private final Map<Player, Integer> playerFiguresLeft;
    private final ScoreBoard scoreBoard;

    private Tile drawnTile;

    public Game(UUID lobbyId, String lobbyName, GameSettings settings, Set<Player> players, EventHandler eventHandler) {
        super(lobbyId, eventHandler);
        this.name = lobbyName;
        this.settings = settings;
        // Transforming the players set into a list might be
        // necessary if a player leaves the game while the
        // game is running. A set might trigger a rehash and
        // shuffle the order of players.
        this.gameLogic = new GameLogic(getId(), settings.getGameMode(), new LinkedList<>(players), eventHandler);
        this.board = Board.create(settings.getGameMode(), settings.getTileList());
        this.board.getBoardGraph().registerEventCallback(this::onRegionClosed);
        this.playerFiguresLeft = new HashMap<>();
        this.scoreBoard = new ScoreBoard(board.getBoardGraph(), players);

        players.forEach(player -> playerFiguresLeft.put(player, FIGURES_PER_PLAYER));
    }

    public static Game from(GameLobby lobby) {
        return new Game(
                lobby.getId(),
                lobby.getName(),
                GameSettings.from(lobby.getLobbySettings()),
                lobby.getPlayers(),
                lobby.eventHandler()
        );
    }

    @Override
    protected void init() {
        triggerLocalEvent(getId(), GameEvent.GAME_STARTED, this);
        gameLogic.initialize();
        gameLogic.setGameEndCondition(board::hasNextTile);
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

    public List<Figure> getFigures() {
        return this.board.getFigures();
    }

    public int getFiguresLeftForPlayer(Player player) {
        return this.playerFiguresLeft.get(player);
    }

    public Map<Player, Integer> getFiguresLeft() {
        return this.playerFiguresLeft;
    }

    public String getName() {
        return this.name;
    }

    public ScoreBoard getScoreBoard() {
        return this.scoreBoard;
    }

    @TestOnly
    void setGameState(GameState gameState) {
        while(gameLogic.getGameState() != gameState) {
            gameLogic.nextPhase();
        }
    }

    @TestOnly
    void setFiguresLeftForPlayer(Player player, int figuresLeft) {
        this.playerFiguresLeft.put(player, figuresLeft);
    }

    @TestOnly
    public void setDrawnTile(Tile tile) {
        this.drawnTile = tile;
    }

    // ========== ACTIONS =========

    public Tile drawTile(Player player) {
        drawnTile = gameAction(player, GameState.DRAW, board::getNextTile);
        return drawnTile;
    }

    public void placeTile(Player player, Tile tile, int x, int y) {
        gameAction(player, GameState.PLACE_TILE, () -> {
            this.board.insertTileToBoard(tile, x, y);
            triggerLocalEvent(getId(), GameEvent.TILE_PLACED, tile, x, y);
        });
        drawnTile = null;
    }

    public List<Integer> getMatchingTileRotations(Tile tile, int x, int y) {
        return this.board.getMatchingRotations(tile, x, y);
    }

    public void placeFigure(Player player, Tile tile, int row, int column) throws RegionOccupiedException, NoFiguresLeftException {
        var figuresLeft = playerFiguresLeft.get(player);
        if (figuresLeft == 0) {
            throw new NoFiguresLeftException(player);
        }

        Optional<RegionOccupiedException> innerException = gameAction(player, GameState.PLACE_FIGURE, () -> {
            try {
                board.placeFigureOnTile(Figure.create(player, tile, row, column));
                triggerLocalEvent(getId(), GameEvent.FIGURE_PLACED, player, tile, row, column);
            } catch (RegionOccupiedException e) {
                return Optional.of(e);
            }
            return Optional.empty();
        });

        if (innerException.isPresent()) {
            throw innerException.get();
        }

        playerFiguresLeft.put(player, figuresLeft - 1);
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

    private void onRegionClosed(TileContent regionType, Set<Set<Graph.Node>> closedRegions) {
        var figures = board.getFigures();
        for (Set<Graph.Node> closedRegion : closedRegions) {
            var figuresToRemove = scoreBoard.assignScoresForClosedRegion(regionType, closedRegion, figures);
            returnFiguresToPlayerPools(figuresToRemove);
        }
    }

    private void returnFiguresToPlayerPools(Set<Figure> figures) {
        figures.forEach(figure -> {
            var owner = figure.getOwner();
            var figuresLeftForPlayer = playerFiguresLeft.get(owner);
            playerFiguresLeft.put(owner, figuresLeftForPlayer + 1);
            board.getFigures().remove(figure);
        });
    }
}
