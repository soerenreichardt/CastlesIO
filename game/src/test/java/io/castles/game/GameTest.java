package io.castles.game;

import io.castles.core.GameMode;
import io.castles.game.events.GameEvent;
import io.castles.util.CollectingEventConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    Game game;
    CollectingEventConsumer eventConsumer;

    @BeforeEach
    void setup() {
        var eventHandler = Server.getInstance().eventHandler();
        var gameSettings = GameSettings.from(GameLobbySettings.builder().gameMode(GameMode.DEBUG).build());
        eventConsumer = new CollectingEventConsumer();
        game = new Game(UUID.randomUUID(), gameSettings, Set.of(new Player("P1"), new Player("P2")), eventHandler);
        eventHandler.registerLocalEventConsumer(game.getId(), eventConsumer);
        game.initialize();
        eventConsumer.reset();
    }

    @Test
    void shouldDrawATile() {
        var activePlayer = game.getActivePlayer();
        var tile = game.getNewTile(activePlayer);
        assertThat(tile).isNotNull();
        assertPhaseSwitched(GameState.DRAW, GameState.PLACE_TILE);
    }

    @Test
    void shouldThrowIfDrawByNotActivePlayer() {
        Player notActivePlayer = new Player("OtherPlayer");
        assertThatThrownBy(() -> game.getNewTile(notActivePlayer))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not the active player");
    }

    @Test
    void shouldPlaceATile() {
        var activePlayer = game.getActivePlayer();
        var tile = game.getNewTile(activePlayer);
        game.placeTile(activePlayer, tile, 0, 1);
        assertPhaseSwitched(GameState.PLACE_TILE, GameState.PLACE_FIGURE);
    }

    private void assertPhaseSwitched(GameState from, GameState to) {
        assertThat(game.getCurrentGameState()).isEqualTo(to);
        assertThat(eventConsumer.events()).containsKey(GameEvent.PHASE_SWITCHED.name());
        assertThat(eventConsumer.events().get(GameEvent.PHASE_SWITCHED.name())).contains(String.join(", ", from.toString(), to.toString()));
    }
}