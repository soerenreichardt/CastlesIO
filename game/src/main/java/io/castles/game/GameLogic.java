package io.castles.game;

import io.castles.core.GameMode;
import io.castles.game.events.EventHandler;
import io.castles.game.events.GameEvent;
import io.castles.game.events.StatefulObject;
import lombok.Getter;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public class GameLogic extends StatefulObject {

    private final EventHandler eventHandler;
    private final GameMode gameMode;

    private List<Player> players;
    private GameState gameState;
    private Player activePlayer;
    private int activePlayerIndex;

    public GameLogic(UUID id, GameMode gameMode, List<Player> players, EventHandler eventHandler) {
        super(id, eventHandler);
        this.gameMode = gameMode;
        this.players = players;
        this.eventHandler = eventHandler;
        this.gameState = GameState.START;
        this.activePlayer = chooseRandomStartPlayer();
    }

    @Override
    protected void init() {
        assert getGameState() == GameState.START;
        nextPhase();
        triggerLocalEvent(getId(), GameEvent.ACTIVE_PLAYER_SWITCHED, activePlayer);
    }

    public void skipPhase() {
        if (gameState.isSkippable()) {
            gameState.advance();
            return;
        }
        throw new IllegalArgumentException(String.format("Unable to skip phase %s", gameState));
    }

    public void nextPhase() {
        var previousGameState = gameState;
        this.gameState = gameState.advance();
        triggerLocalEvent(getId(), GameEvent.PHASE_SWITCHED, previousGameState, gameState);
        if (gameState == GameState.NEXT_PLAYER) {
            nextPlayer();
            nextPhase();
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
