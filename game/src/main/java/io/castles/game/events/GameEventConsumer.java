package io.castles.game.events;

import io.castles.core.tile.Tile;
import io.castles.game.Game;
import io.castles.game.GameLobbySettings;
import io.castles.game.GameState;
import io.castles.game.Player;

public interface GameEventConsumer {

    void onPlayerAdded(Player player);

    void onPlayerRemoved(Player player);

    void onSettingsChanged(GameLobbySettings gameLobbySettings);

    void onGameStarted(Game game);

    void onActivePlayerSwitched(Player activePlayer);

    void onPhaseSwitched(GameState from, GameState to);

    void onTilePlaced(Tile tile, int x, int y);

    void onFigurePlaced(Player owner, Tile tile, int row, int column);

    void onGameEnd();

    class Adapter implements GameEventConsumer {
        @Override
        public void onPlayerAdded(Player player) {

        }

        @Override
        public void onPlayerRemoved(Player player) {

        }

        @Override
        public void onSettingsChanged(GameLobbySettings gameLobbySettings) {

        }

        @Override
        public void onGameStarted(Game game) {

        }

        @Override
        public void onActivePlayerSwitched(Player activePlayer) {

        }

        @Override
        public void onPhaseSwitched(GameState from, GameState to) {

        }

        @Override
        public void onTilePlaced(Tile tile, int x, int y) {

        }

        @Override
        public void onFigurePlaced(Player owner, Tile tile, int row, int column) {

        }

        @Override
        public void onGameEnd() {

        }
    }
}
