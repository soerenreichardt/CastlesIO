package io.castles.game;

import io.castles.core.board.Board;
import io.castles.core.graph.Graph;
import io.castles.core.tile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreBoardTest {

    Board board;
    Set<Player> players;
    ScoreBoard scoreBoard;

    @BeforeEach
    void setup() {
        var startMatrix = new Matrix<>(3, 3, new TileContent[]{
                TileContent.GRAS, TileContent.GRAS, TileContent.SHARED,
                TileContent.GRAS, TileContent.GRAS, TileContent.CASTLE,
                TileContent.GRAS, TileContent.GRAS, TileContent.SHARED,
        });
        this.board = Board.withSpecificTile(new MatrixTileLayout(startMatrix));
        this.players = Set.of(new Player("P1"), new Player("P2"));
        this.scoreBoard = new ScoreBoard(board.getBoardGraph(), players);
    }

    @Test
    void shouldAddScoreForPlayer() {
        var player = players.iterator().next();

        scoreBoard.addScoreForPlayer(player, 5);
        assertThat(scoreBoard.getScoreForPlayer(player)).isEqualTo(5);

        scoreBoard.addScoreForPlayer(player, 3);
        assertThat(scoreBoard.getScoreForPlayer(player)).isEqualTo(8);
    }

    @Test
    void shouldAssignScoresForClosedRegions() {
        var startTile = board.getTile(0, 0);
        var tile = new Tile(new MatrixTileLayout(startTile.<MatrixTileLayout>getTileLayout().getContent()));
        tile.rotate();
        tile.rotate();

        board.insertTileToBoard(tile, 1, 0);

        var closedRegionNodeSet = Set.of(
                new Graph.Node(0, 0, 1, 2),
                new Graph.Node(1, 0, 1, 0)
        );
        var player = players.iterator().next();
        var playerFigure = new Figure(new Graph.Node(1, 0, 1, 0), player);
        var figuresToRemove = scoreBoard.assignScoresForClosedRegion(TileContent.CASTLE, closedRegionNodeSet, List.of(playerFigure));

        assertThat(figuresToRemove).contains(playerFigure);
        assertThat(scoreBoard.getScoreForPlayer(player)).isEqualTo(4);
    }

}