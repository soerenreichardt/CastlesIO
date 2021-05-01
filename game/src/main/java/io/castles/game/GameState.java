package io.castles.game;

public enum GameState {
    START(0, Flags.INITIALIZATION),
    DRAW(1),
    PLACE_TILE(2),
    PLACE_FIGURE(3, Flags.SKIPPABLE),
    NEXT_PLAYER(4),
    GAME_END(5, Flags.END_STEP) {
        @Override
        public GameState advance() {
            return GAME_END;
        }
    };

    static class Flags {
        static final int INITIALIZATION = 1;
        static final int SKIPPABLE = 1 << 1;
        static final int END_STEP = 1 << 2;
    }

    private final int id;
    private int flags;
    private boolean gameEnd;

    GameState(int id, int... flags) {
        this.id = id;
        this.flags = 0;
        this.gameEnd = false;

        for (int flag : flags) {
            this.flags |= flag;
        }
    }

    public boolean isInitializationStep() {
        return (this.flags & Flags.INITIALIZATION) != 0;
    }

    public boolean isSkippable() {
        return (this.flags & Flags.SKIPPABLE) != 0;
    }

    public boolean isEndStep() {
        return (this.flags & Flags.END_STEP) != 0;
    }

    public GameState advance() {
        if (gameEnd) {
            return GAME_END;
        }

        int numGameStates = values().length;
        int nextId = (id + 1) % numGameStates;
        for (int i = 0; i < numGameStates - 1; i++) {
            var nextState = values()[nextId];
            if (nextState.isInitializationStep() || nextState.isEndStep()) {
                nextId = (nextId + 1) % numGameStates;
            } else {
                return nextState;
            }
        }
        throw new IllegalStateException("Couldn't find the next game state.");
    }

    public void endGame() {
        this.gameEnd = true;
    }
}
