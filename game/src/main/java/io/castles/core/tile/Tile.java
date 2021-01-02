package io.castles.core.tile;

import io.castles.game.IdentifiableObject;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.TestOnly;

import java.util.Random;
import java.util.UUID;

import static io.castles.core.tile.TileUtil.oppositeDirection;

@EqualsAndHashCode(callSuper = true)
public class Tile extends IdentifiableObject {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;

    public static final int NUM_BORDERS = 4;
    public static final int NUM_NEIGHBORS = 4;

    private AbstractTile delegate;

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

    private Tile(AbstractTile delegate) {
        this.delegate = delegate;
    }

    /**
     * This constructor should only be used to construct
     * a Tile from a TileDTO.
     */
    public Tile(UUID id, TileBorder[] tileBorders) {
        super(id);
        this.delegate = new DrawnTile(tileBorders);
    }

    public Tile[] getNeighbors() {
        return delegate.neighbors();
    }

    public TileBorder[] getTileBorders() {
        return delegate.tileBorders();
    }

    public boolean matches(Tile other, int direction) {
        if (other == null) {
            return true;
        }
        return delegate.tileBorders()[direction] == other.getTileBorders()[oppositeDirection(direction)];
    }

    public void insertToBoard(int x, int y) {
        this.delegate = new InsertedTile(delegate.tileBorders(), x, y);
    }

    public void setNeighbor(int position, Tile tile) {
        delegate.setNeighbor(position, tile);
    }

    public int getX() {
        return delegate.getX();
    }

    public int getY() {
        return delegate.getY();
    }

    public void rotate() {
        delegate.rotate();
    }

    static class DrawnTile extends AbstractTile {

        protected DrawnTile(TileBorder[] tileBorders) {
            super(tileBorders);
        }

        @Override
        public void setNeighbor(int position, Tile tile) {
            throw new UnsupportedOperationException("setNeighbor is not supported on an uninserted tile.");
        }

        @Override
        public int getX() {
            throw new UnsupportedOperationException("getX is not supported on an uninserted tile.");
        }

        @Override
        public int getY() {
            throw new UnsupportedOperationException("getY is not supported on an uninserted tile.");
        }

        @Override
        public void rotate() {
            TileBorder leftBorder = tileBorders[LEFT];
            tileBorders[LEFT] = tileBorders[BOTTOM];
            tileBorders[BOTTOM] = tileBorders[RIGHT];
            tileBorders[RIGHT] = tileBorders[TOP];
            tileBorders[TOP] = leftBorder;
        }

        @Override
        public TileBorder[] tileBorders() {
            return tileBorders;
        }

        @Override
        public Tile[] neighbors() {
            throw new UnsupportedOperationException("neighbors is not supported on an uninserted tile.");
        }
    }

    static class InsertedTile extends AbstractTile {

        private final Tile[] neighbors;

        private final int x;
        private final int y;

        public InsertedTile(TileBorder[] tileBorders, int x, int y) {
            this(tileBorders, new Tile[NUM_NEIGHBORS], x, y);
        }

        public InsertedTile(TileBorder[] tileBorders, Tile[] neighbors, int x, int y) {
            super(tileBorders);
            this.x = x;
            this.y = y;
            this.neighbors = neighbors;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public void setNeighbor(int position, Tile tile) {
            this.neighbors[position] = tile;
        }

        @Override
        public void rotate() {
            throw new UnsupportedOperationException("Rotate on inserted tile");
        }

        @Override
        public TileBorder[] tileBorders() {
            return tileBorders;
        }

        @Override
        public Tile[] neighbors() {
            return neighbors;
        }
    }
}
