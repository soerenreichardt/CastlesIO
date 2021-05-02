package io.castles.core.board;

import io.castles.core.tile.Tile;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TileIterators {

    public interface TileIterator extends Iterator<Tile> {
        int getNumTilesLeft();
    }

    static class RandomList implements TileIterator {

        private final List<Tile> tileList;
        private final Random random;

        public RandomList(List<Tile> tileList) {
            this.tileList = new LinkedList<>(tileList);
            this.random = new Random();
        }

        @Override
        public int getNumTilesLeft() {
            return tileList.size();
        }

        @Override
        public boolean hasNext() {
            return !tileList.isEmpty();
        }

        @Override
        public Tile next() {
            var tile = tileList.get(random.nextInt(tileList.size()));
            tileList.remove(tile);
            return tile;
        }
    }

    static class Static implements TileIterator {

        private final Tile tile;

        public Static(Tile tile) {
            this.tile = tile;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Tile next() {
            return tile;
        }

        @Override
        public int getNumTilesLeft() {
            return 1;
        }
    }

}
