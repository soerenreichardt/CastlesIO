package io.castles.game;

import io.castles.core.board.Board;
import io.castles.core.board.BoardGraph;
import io.castles.core.graph.Graph;
import io.castles.core.tile.Figure;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.game.events.GameEvent;
import io.castles.game.events.GameEventConsumer;
import io.castles.game.events.LocalEventHandler;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreBoard extends GameEventConsumer.Adapter {

    private static final Map<TileContent, Integer> TILE_CONTENT_VALUE = Map.of(
            TileContent.STREET, 1,
            TileContent.CASTLE, 2,
            TileContent.GRAS, 3
    );

    private final Map<Player, Integer> playerScores;
    private final BoardGraph boardGraph;
    private final List<Figure> figures;
    private final LocalEventHandler eventHandler;

    public static ScoreBoard create(Board board, Set<Player> players, LocalEventHandler eventHandler) {
        return new ScoreBoard(board.getBoardGraph(), board.getFigures(), players, eventHandler);
    }

    ScoreBoard(BoardGraph boardGraph, List<Figure> figures, Set<Player> players, LocalEventHandler eventHandler) {
        this.boardGraph = boardGraph;
        this.figures = figures;
        this.playerScores = new HashMap<>();
        this.eventHandler = eventHandler;

        players.forEach(player -> playerScores.put(player, 0));
    }

    @Override
    public void onGameEnd() {
        computePawnScores(figures);
        // TODO: compute winning player and trigger event
    }

    public void computePawnScores(List<Figure> figures) {
        var closedCastlesPerGrasComponents = boardGraph.closedCastlesAdjacentToGraphComponent();
        closedCastlesPerGrasComponents.forEach(closedCastlesForGrasComponent -> {
            var grasComponent = closedCastlesForGrasComponent.getGrasComponent();
            assignScoresForClosedRegion(TileContent.GRAS, grasComponent, figures);
        });
    }

    public void addScoreForPlayer(Player player, int score) {
        playerScores.put(player, playerScores.get(player) + score);
    }

    public void addScoreForPlayers(Set<Player> players, int score) {
        players.forEach(player -> addScoreForPlayer(player, score));
    }

    public int getScoreForPlayer(Player player) {
        return playerScores.get(player);
    }

    public Set<Figure> assignScoresForClosedRegion(TileContent regionType, Set<Graph.Node> closedRegion, List<Figure> figures) {
        Map<Player, Integer> figuresInRegion = new HashMap<>();
        Set<Figure> figuresToRemove = new HashSet<>();
        for (Figure figure : figures) {
            if (closedRegion.contains(figure.getPosition())) {
                figuresInRegion.put(figure.getOwner(), figuresInRegion.getOrDefault(figure.getOwner(), 0) + 1);
                figuresToRemove.add(figure);
            }
        }

        var playersWithMostFiguresInRegion = findPlayersWithMostFiguresInRegion(figuresInRegion);
        var distinctTilesInNodeSet = boardGraph.distinctTilesInNodeSet(closedRegion);

        assignScoresForPlayer(regionType, distinctTilesInNodeSet, playersWithMostFiguresInRegion);

        return figuresToRemove;
    }

    private void assignScoresForPlayer(TileContent regionType, Set<Tile> distinctClosedTiles, Set<Player> players) {
        var score = distinctClosedTiles.size() * TILE_CONTENT_VALUE.get(regionType);
        addScoreForPlayers(players, score);
    }

    private static Set<Player> findPlayersWithMostFiguresInRegion(Map<Player, Integer> figuresInRegion) {
        var comparator = Comparator.<Map.Entry<Player, Integer>>comparingInt(Map.Entry::getValue).reversed();
        var sortedMapEntries = figuresInRegion
                .entrySet()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        var entryIterator = sortedMapEntries.iterator();

        if (!entryIterator.hasNext()) {
            return Set.of();
        }

        var playerWithMostFigures = entryIterator.next();

        Set<Player> playersWithMostFigures = new HashSet<>();
        playersWithMostFigures.add(playerWithMostFigures.getKey());

        Map.Entry<Player, Integer> nextEntry;
        while (entryIterator.hasNext() && (nextEntry = entryIterator.next()).getValue().intValue() == playerWithMostFigures.getValue().intValue()) {
            playersWithMostFigures.add(nextEntry.getKey());
        }
        return playersWithMostFigures;
    }
}
