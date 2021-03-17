package io.castles.game;

import org.jetbrains.annotations.TestOnly;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final Server INSTANCE = new Server();
    public static Server getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, Game> activeGames;
    private final Map<UUID, GameLobby> activeLobbies;

    private Server() {
        this.activeGames = new ConcurrentHashMap<>();
        this.activeLobbies = new ConcurrentHashMap<>();
    }

    public GameLobby createGameLobby(String name, Player owner) {
        var gameLobby = new GameLobby(name, owner);
        activeLobbies.put(gameLobby.getId(), gameLobby);
        return gameLobby;
    }

    public GameLobby gameLobbyById(UUID id) {
        if (!activeLobbies.containsKey(id)) {
            throw new IllegalArgumentException(String.format("No lobby with id %s was found.", id));
        }
        return activeLobbies.get(id);
    }

    public Game gameById(UUID id) {
        if (!activeGames.containsKey(id)) {
            throw new IllegalArgumentException(String.format("No game with id %s was found.", id));
        }
        return activeGames.get(id);
    }

    public Game startGame(UUID lobbyId) {
        var gameLobby = gameLobbyById(lobbyId);
        var game = gameLobby.startGame();
        activeGames.put(game.getId(), game);
        activeLobbies.remove(lobbyId);
        return game;
    }

    public Collection<Game> getActiveGames() {
        return this.activeGames.values();
    }

    public Collection<GameLobby> getActiveGameLobbies() {
        return this.activeLobbies.values();
    }

    @TestOnly
    void reset() {
        activeLobbies.clear();
        activeGames.clear();
    }
}
