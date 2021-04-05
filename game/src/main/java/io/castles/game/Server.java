package io.castles.game;

import io.castles.game.events.EventHandler;
import io.castles.game.events.GameEvent;
import org.jetbrains.annotations.TestOnly;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Server {

    private static final Server INSTANCE = new Server();
    public static Server getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, Game> activeGames;
    private final Map<UUID, GameLobby> activeLobbies;
    private final EventHandler eventHandler;

    private Server() {
        this.activeGames = new ConcurrentHashMap<>();
        this.activeLobbies = new ConcurrentHashMap<>();
        this.eventHandler = new EventHandler();
    }

    public GameLobby createGameLobby(String name, Player owner) {
        var gameLobby = new GameLobby(name, owner, eventHandler);
        activeLobbies.put(gameLobby.getId(), gameLobby);
        gameLobby.initialize();
        return gameLobby;
    }

    public GameLobby gameLobbyById(UUID id) {
        if (!activeLobbies.containsKey(id)) {
            throw new IllegalArgumentException(String.format("No lobby with id %s was found.", id));
        }
        return activeLobbies.get(id);
    }

    public List<GameLobby> publicGameLobbies() {
        return activeLobbies.values().stream().filter(GameLobby::isPublic).collect(Collectors.toList());
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
        game.initialize();
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

    public EventHandler eventHandler() {
        return eventHandler;
    }

    @TestOnly
    public void addGameLobby(GameLobby gameLobby) {
        this.activeLobbies.put(gameLobby.getId(), gameLobby);
    }

    @TestOnly
    public void addGame(Game game) {
        this.activeGames.put(game.getId(), game);
    }

    @TestOnly
    public void reset() {
        activeLobbies.clear();
        activeGames.clear();
    }
}
