package io.castles.game;

import io.castles.core.board.BoardGraph;
import io.castles.core.graph.Graph;
import io.castles.core.tile.Figure;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreBoard {

    private static final Map<TileContent, Integer> TILE_CONTENT_VALUE = Map.of(
            TileContent.STREET, 1,
            TileContent.CASTLE, 2
    );

    private final Map<Player, Integer> playerScores;
    private final BoardGraph boardGraph;

    public ScoreBoard(BoardGraph boardGraph, Set<Player> players) {
        this.boardGraph = boardGraph;
        this.playerScores = new HashMap<>();

        players.forEach(player -> playerScores.put(player, 0));
    }

    public void addScoreForPlayer(Player player, int score) {
        playerScores.put(player, playerScores.get(player) + score);
    }

    public void addScoreForPlayers(Set<Player> players, int score) {
        players.forEach(player -> addScoreForPlayer(player, score));
    }

    public Set<Figure> assignScoresForClosedRegion(TileContent regionType, Set<Graph.Node> closedRegion, List<Figure> figures) {
        Map<Player, Integer> figuresInRegion = new HashMap<>();
        Set<Figure> figuresToRemove = new HashSet<>();
        for (Figure figure : figures) {
            if (closedRegion.contains(figure.getPosition())) {
                figuresInRegion.computeIfAbsent(figure.getOwner(), player -> figuresInRegion.getOrDefault(player, 0) + 1);
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
        var playerWithMostFigures = entryIterator.next();

        Set<Player> playersWithMostFigures = new HashSet<>();
        playersWithMostFigures.add(playerWithMostFigures.getKey());

        Map.Entry<Player, Integer> nextEntry;
        while ((nextEntry = entryIterator.next()).getValue().intValue() == playerWithMostFigures.getValue().intValue()) {
            playersWithMostFigures.add(nextEntry.getKey());
        }
        return playersWithMostFigures;
    }
}
