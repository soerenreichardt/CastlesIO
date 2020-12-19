package io.castles.game;

public enum GameState {
    START(0, Flags.INITIALIZATION),
    DRAW(1),
    PLACE_TILE(2),
    PLACE_FIGURE(3, Flags.SKIPPABLE),
    NEXT_PLAYER(4);

    static class Flags {
        static final int INITIALIZATION = 1;
        static final int SKIPPABLE = 1 << 1;
    }

    private final int id;
    private int flags;

    GameState(int id, int... flags) {
        this.id = id;
        this.flags = 0;
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

    public GameState advance() {
        int numGameStates = values().length;
        int nextId = (id + 1) % numGameStates;
        for (int i = 0; i < numGameStates - 1; i++) {
            if (values()[nextId].isInitializationStep()) {
                nextId = (nextId + 1) % numGameStates;
            } else {
                return values()[nextId];
            }
        }
        throw new IllegalStateException("Couldn't find the next game state.");
    }
}
