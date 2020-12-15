package io.castles.core;

import org.jetbrains.annotations.TestOnly;

import java.util.Random;

public class Tile {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;

    public static final int NUM_BORDERS = 4;
    public static final int NUM_NEIGHBORS = 4;

    private final TileBorder[] tileBorders;
    protected Tile[] neighbors;

    private Tile delegate;

    public static Tile drawRandom() {
        TileBorder[] tileBorders = new TileBorder[NUM_BORDERS];
        Random rng = new Random();
        for (int i = 0; i < tileBorders.length; i++) {
            tileBorders[i] = TileBorder.getById(rng.nextInt(TileBorder.values().length));
        }
        return new Tile(new DrawnTile(tileBorders));
    }

    @TestOnly
    public static Tile drawStatic(TileBorder border) {
        return new Tile(new DrawnTile(new TileBorder[]{ border, border, border, border }));
    }

    @TestOnly
    public static Tile drawSpecific(
            TileBorder leftBorder,
            TileBorder rightBorder,
            TileBorder topBorder,
            TileBorder bottomBorder
    ) {
        return new Tile(new DrawnTile(new TileBorder[]{ leftBorder, rightBorder, topBorder, bottomBorder }));
    }

    private Tile(Tile delegate) {
        this(delegate.tileBorders);
        this.delegate = delegate;
    }

    protected Tile(TileBorder[] tileBorders) {
        this.tileBorders = tileBorders;
        this.neighbors = new Tile[NUM_NEIGHBORS];
    }

    public Tile[] getNeighbors() {
        return this.neighbors;
    }

    public TileBorder[] getTileBorders() {
        return this.tileBorders;
    }

    protected void insertToBoard(int x, int y) {
        this.delegate = new InsertedTile(tileBorders, x, y);
    }

    protected void setNeighbor(int position, Tile tile) {
        delegate.setNeighbor(position, tile);
    }

    protected int getX() {
        return delegate.getX();
    }

    protected int getY() {
        return delegate.getY();
    }

    static class DrawnTile extends Tile {

        protected DrawnTile(TileBorder[] tileBorders) {
            super(tileBorders);
        }

        @Override
        protected void setNeighbor(int position, Tile tile) {
            throw new UnsupportedOperationException("setNeighbor is not supported on an uninserted tile.");
        }

        @Override
        protected int getX() {
            throw new UnsupportedOperationException("getX is not supported on an uninserted tile.");
        }

        @Override
        protected int getY() {
            throw new UnsupportedOperationException("getY is not supported on an uninserted tile.");
        }
    }

    static class InsertedTile extends Tile {

        private final int x;
        private final int y;

        public InsertedTile(TileBorder[] tileBorders, int x, int y) {
            super(tileBorders);
            this.x = x;
            this.y = y;
        }

        @Override
        protected void insertToBoard(int x, int y) {
            throw new UnsupportedOperationException("Tile is already inserted to the board");
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setNeighbor(int position, Tile tile) {
            this.neighbors[position] = tile;
        }
    }

    enum TileBorder {
        GRAS(0),
        CASTLE(1),
        STREET(2);

        private final int id;

        TileBorder(int id) {
            this.id = id;
        }

        int getId() {
            return id;
        }

        static TileBorder getById(int id) {
            for (TileBorder tileBorder : values()) {
                if (tileBorder.getId() == id) {
                    return tileBorder;
                }
            }

            throw new IllegalArgumentException(String.format("Id %d is not within range (0,%d)", id, values().length));
        }
    }
}
