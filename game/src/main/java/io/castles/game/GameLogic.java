package io.castles.game;

import io.castles.core.GameMode;

public class GameLogic {

    private final GameMode gameMode;
    private GameState gameState;

    public GameLogic(GameMode gameMode) {
        this.gameMode = gameMode;
        this.gameState = GameState.START;
    }

    public GameState getGameState() {
        return this.gameState;
    }
}
