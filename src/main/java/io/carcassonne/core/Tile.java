package io.carcassonne.core;

public class Tile {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;

    public static final int NUM_BORDERS = 4;
    public static final int NUM_NEIGHBORS = 4;

    private TileBorder[] tileBorders;
    protected Tile[] neighbors;

    private Tile delegate;

    public static Tile drawRandom() {
        return new Tile(new DrawnTile());
    }

    private Tile(Tile delegate) {
        this();
        this.delegate = delegate;
    }

    public Tile[] getNeighbors() {
        return this.neighbors;
    }

    protected Tile() {
        this.tileBorders = new TileBorder[NUM_BORDERS];
        this.neighbors = new Tile[NUM_NEIGHBORS];

        // TODO initialize random tile borders
    }

    protected void insertToBoard(int x, int y) {
        this.delegate = new InsertedTile(x, y);
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

        public InsertedTile(int x, int y) {
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
        GRAS,
        CASTLE,
        STREET
    }
}
