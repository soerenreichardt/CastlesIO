package io.castles.game.events;

public enum GameEvent {
    // Lobby
    PLAYER_ADDED,
    PLAYER_REMOVED,
    SETTINGS_CHANGED,
    LOBBY_CREATED,
    // Game
    GAME_STARTED,
    PHASE_SWITCHED,
    ACTIVE_PLAYER_SWITCHED,
    TILE_PLACED,
    FIGURE_PLACED,
    SCORE_CHANGED,
    GAME_END
}
