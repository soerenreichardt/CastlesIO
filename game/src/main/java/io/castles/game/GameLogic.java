package io.castles.game;

import io.castles.core.GameMode;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.Random;

@Value
public class GameLogic {

    GameMode gameMode;
    List<Player> players;

    @NonFinal GameState gameState;
    @NonFinal Player activePlayer;
    @NonFinal int activePlayerIndex;

    public GameLogic(GameMode gameMode, List<Player> players) {
        this.gameMode = gameMode;
        this.players = players;
        this.gameState = GameState.START;
        this.activePlayer = chooseRandomStartPlayer();
    }

    public void skipPhase() {
        if (gameState.isSkippable()) {
            gameState.advance();
            return;
        }
        throw new IllegalArgumentException(String.format("Unable to skip phase %s", gameState));
    }

    public void nextPhase() {
        this.gameState = gameState.advance();
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
    }
}
