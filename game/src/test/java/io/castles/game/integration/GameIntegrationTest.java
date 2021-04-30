package io.castles.game.integration;

import io.castles.core.GameMode;
import io.castles.core.tile.Matrix;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.exceptions.NoFiguresLeftException;
import io.castles.exceptions.RegionOccupiedException;
import io.castles.game.Player;
import io.castles.game.Server;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameIntegrationTest {

    Server server = Server.getInstance();

    @Test
    void shouldPlayGame() throws NoFiguresLeftException, RegionOccupiedException {
        var owner = new Player("P1");
        var gameLobby = server.createGameLobby("Test", owner);
        var otherPlayer = new Player("P2");
        gameLobby.addPlayer(otherPlayer);
        gameLobby.setGameMode(GameMode.DEBUG);

        var game = server.startGame(gameLobby.getId());

        // Turn 1

        var activePlayer = game.getActivePlayer();
        game.drawTile(activePlayer);

        var streetStartTile = Tile.fromMatrix(new Matrix<>(3, 3, new TileContent[]{
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                TileContent.GRAS, TileContent.STREET, TileContent.STREET,
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS
        }));

        game.setDrawnTile(streetStartTile);
        game.placeTile(activePlayer, streetStartTile, 1, 0);
        game.placeFigure(activePlayer, streetStartTile, 1, 1);

        // Turn 2

        activePlayer = game.getActivePlayer();
        game.drawTile(activePlayer);
        var streetEndTile = Tile.fromMatrix(new Matrix<>(3, 3, new TileContent[] {
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS,
                TileContent.STREET, TileContent.STREET, TileContent.GRAS,
                TileContent.GRAS, TileContent.GRAS, TileContent.GRAS
        }));
        game.setDrawnTile(streetEndTile);
        game.placeTile(activePlayer, streetEndTile, 2, 0);
        game.skipPhase(activePlayer);

        var scoreBoard = game.getScoreBoard();
        assertThat(scoreBoard.getScoreForPlayer(activePlayer)).isEqualTo(0);
        assertThat(scoreBoard.getScoreForPlayer(game.getActivePlayer())).isEqualTo(2);
    }

}
