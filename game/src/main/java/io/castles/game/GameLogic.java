package io.castles.game;

import io.castles.core.GameMode;
import io.castles.game.events.EventHandler;
import io.castles.game.events.GameEvent;
import io.castles.game.events.StatefulObject;
import lombok.Getter;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.BooleanSupplier;

@Getter
public class GameLogic extends StatefulObject {

    private final EventHandler eventHandler;
    private final GameMode gameMode;

    private List<Player> players;
    private GameState gameState;
    private Player activePlayer;
    private int activePlayerIndex;
    private BooleanSupplier gameEndCondition;

    public GameLogic(UUID id, GameMode gameMode, List<Player> players, EventHandler eventHandler) {
        super(id, eventHandler);
        this.gameMode = gameMode;
        this.players = players;
        this.eventHandler = eventHandler;
        this.gameState = GameState.START;
        this.activePlayer = chooseRandomStartPlayer();
        this.gameEndCondition = () -> false;
    }

    @Override
    protected void init() {
        assert getGameState() == GameState.START;
        nextPhase();
        triggerLocalEvent(getId(), GameEvent.ACTIVE_PLAYER_SWITCHED, activePlayer);
    }

    @Override
    public void restart() {
        gameState = GameState.START;
        activePlayer = chooseRandomStartPlayer();
        initialize();
    }

    public void setGameEndCondition(BooleanSupplier endCondition) {
        this.gameEndCondition = endCondition;
    }

    public void skipPhase() {
        if (!gameState.isSkippable()) {
            throw new IllegalArgumentException(String.format("Unable to skip phase %s", gameState));
        }
    }

    public void nextPhase() {
        var previousGameState = gameState;
        this.gameState = gameState.advance();
        triggerLocalEvent(getId(), GameEvent.PHASE_SWITCHED, previousGameState, gameState);
        if (gameState == GameState.NEXT_PLAYER) {
            if (gameEndCondition.getAsBoolean()) {
                gameState.endGame();
                nextPhase();
                triggerLocalEvent(getId(), GameEvent.GAME_END);
            } else {
                nextPlayer();
                nextPhase();
            }
        }
    }

    private Player chooseRandomStartPlayer() {
        Random rand = new Random();
        this.activePlayerIndex = rand.nextInt(players.size());
        return players.get(activePlayerIndex);
    }

    private void nextPlayer() {
        this.activePlayerIndex = (activePlayerIndex + 1) % players.size();
        this.activePlayer = players.get(activePlayerIndex);
        triggerLocalEvent(getId(), GameEvent.ACTIVE_PLAYER_SWITCHED, activePlayer);
    }

}
