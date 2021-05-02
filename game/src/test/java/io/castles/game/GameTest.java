package io.castles.game;

import io.castles.core.GameMode;
import io.castles.core.tile.Figure;
import io.castles.exceptions.NoFiguresLeftException;
import io.castles.exceptions.RegionOccupiedException;
import io.castles.game.events.GameEvent;
import io.castles.util.CollectingEventConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static io.castles.util.CollectingEventConsumer.stringFrom;
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
        game = new Game(UUID.randomUUID(), "Just some lobby name", gameSettings, Set.of(new Player("P1"), new Player("P2")), eventHandler);
        eventHandler.registerLocalEventConsumer(game.getId(), eventConsumer);
        game.initialize();
        eventConsumer.reset();
    }

    @Test
    void shouldDrawATile() {
        var activePlayer = game.getActivePlayer();
        var tile = game.drawTile(activePlayer);
        assertThat(tile).isNotNull();
        assertThat(game.getDrawnTile(activePlayer)).isEqualTo(tile);
        assertPhaseSwitched(GameState.DRAW, GameState.PLACE_TILE);
        assertThat(game.getCurrentGameState()).isEqualTo(GameState.PLACE_TILE);
    }

    @Test
    void shouldThrowIfDrawByNotActivePlayer() {
        Player notActivePlayer = new Player("OtherPlayer");
        assertThatThrownBy(() -> game.drawTile(notActivePlayer))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not the active player");
    }

    @Test
    void shouldPlaceATile() {
        var activePlayer = game.getActivePlayer();
        var tile = game.drawTile(activePlayer);
        game.placeTile(activePlayer, tile, 0, 1);

        assertThat(game.getTile(0, 1)).isEqualTo(tile);

        assertPhaseSwitched(GameState.PLACE_TILE, GameState.PLACE_FIGURE);
        assertThat(game.getCurrentGameState()).isEqualTo(GameState.PLACE_FIGURE);

        assertThat(eventConsumer.events()).containsKey(GameEvent.TILE_PLACED.name());
        assertThat(eventConsumer.events().get(GameEvent.TILE_PLACED.name())).contains(stringFrom(tile, 0, 1, 1));
    }

    @Test
    void shouldBeAbleToSkipFigurePlacingAndProceedWithNextTurn() {
        game.setGameState(GameState.PLACE_FIGURE);
        var activePlayer = game.getActivePlayer();
        game.skipPhase(activePlayer);
        assertPhaseSwitched(GameState.PLACE_FIGURE, GameState.NEXT_PLAYER);

        // next turn
        assertThat(game.getCurrentGameState()).isEqualTo(GameState.DRAW);
        assertPhaseSwitched(GameState.NEXT_PLAYER, GameState.DRAW);
        assertThat(game.getActivePlayer()).isNotEqualTo(activePlayer);
    }

    @Test
    void shouldRestartGame() {
        var activePlayer = game.getActivePlayer();
        var tile = game.drawTile(activePlayer);
        game.placeTile(activePlayer, tile, 0, 1);
        assertThat(game.getTile(0, 1)).isEqualTo(tile);

        game.restart();
        assertThat(game.getTile(0, 0)).isEqualTo(game.getStartTile());
        assertThatThrownBy(() -> game.getTile(0, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Nested
    class Figures {

        Player activePlayer;

        @BeforeEach
        void setup() {
            game.setGameState(GameState.PLACE_FIGURE);
            activePlayer = game.getActivePlayer();
        }

        @Test
        void shouldBeAbleToPlaceFigure() throws RegionOccupiedException, NoFiguresLeftException {
            game.placeFigure(activePlayer, game.getStartTile(), 0, 0);
            assertThat(game.getFigures().size()).isEqualTo(1);
            assertThat(game.getFigures().get(0)).isEqualTo(Figure.create(activePlayer, game.getStartTile(), 0, 0));
        }

        @Test
        void shouldThrowWhenPlacingIllegalFigure() throws RegionOccupiedException, NoFiguresLeftException {
            game.placeFigure(activePlayer, game.getStartTile(), 0, 0);
            var nextPlayer = game.getActivePlayer();
            assertThat(nextPlayer).isNotEqualTo(activePlayer);

            game.setGameState(GameState.PLACE_FIGURE);
            assertThatThrownBy(() -> game.placeFigure(nextPlayer, game.getStartTile(), 0, 0))
                    .isInstanceOf(RegionOccupiedException.class);
        }

        @Test
        void shouldRemoveAvailableFigureWhenPlacing() throws RegionOccupiedException, NoFiguresLeftException {
            assertThat(game.getFiguresLeftForPlayer(activePlayer)).isEqualTo(Game.FIGURES_PER_PLAYER);
            game.placeFigure(activePlayer, game.getStartTile(), 0, 0);
            assertThat(game.getFiguresLeftForPlayer(activePlayer)).isEqualTo(Game.FIGURES_PER_PLAYER - 1);
        }

        @Test
        void shouldThrowIfPlayerHasNoFiguresLeft() {
            game.setFiguresLeftForPlayer(activePlayer, 0);
            assertThatThrownBy(() -> game.placeFigure(activePlayer, game.getStartTile(), 0, 0))
                .isInstanceOf(NoFiguresLeftException.class);
        }
    }

    private void assertPhaseSwitched(GameState from, GameState to) {
        assertThat(eventConsumer.events()).containsKey(GameEvent.PHASE_SWITCHED.name());
        assertThat(eventConsumer.events().get(GameEvent.PHASE_SWITCHED.name())).contains(String.join(", ", from.toString(), to.toString()));
    }
}