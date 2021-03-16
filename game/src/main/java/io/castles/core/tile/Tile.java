package io.castles.core.tile;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.TestOnly;

import java.util.concurrent.atomic.AtomicLong;

import static io.castles.core.tile.TileLayout.NUM_NEIGHBORS;

@EqualsAndHashCode
public class Tile {

    private static final long RESERVED_IDS = 20;
    private static final AtomicLong ID_GENERATOR = new AtomicLong(RESERVED_IDS);

    private final long id;
    private AbstractTile delegate;

    @TestOnly
    public static Tile drawStatic(TileContent content) {
        var tileLayout = MatrixTileLayout.builder().setAll(content);
        return new Tile(tileLayout);
    }

    @TestOnly
    public static Tile drawSpecific(
            TileContent leftEdgeContent,
            TileContent rightEdgeContent,
            TileContent topEdgeContent,
            TileContent bottomEdgeContent
    ) {
        var tileLayout = MatrixTileLayout.builder()
                .setBackground(TileContent.GRAS)
                .setTopEdge(topEdgeContent)
                .setBottomEdge(bottomEdgeContent)
                .setLeftEdge(leftEdgeContent)
                .setRightEdge(rightEdgeContent)
                .build();
        return new Tile(tileLayout);
    }

    private Tile(TileLayout tileLayout) {
        this(getNewId(), tileLayout);
    }

    /**
     * This constructor should only be used to construct
     * a Tile from a TileDTO.
     */
    public Tile(long id, TileLayout tileLayout) {
        this.id = id;
        this.delegate = new DrawnTile(tileLayout);
    }

    public Tile[] getNeighbors() {
        return delegate.neighbors();
    }

    public TileContent[] getTileEdges() {
        return delegate.getTileEdgeCenters();
    }

    public <T> T getTileLayout() {
        return (T) delegate.getTileLayout();
    }

    public boolean matches(Tile other, int direction) {
        if (other == null) {
            return true;
        }
        return delegate.getTileLayout().matches(other.getTileLayout(), direction);
    }

    public void insertToBoard(int x, int y) {
        this.delegate = new InsertedTile(delegate.getTileLayout(), x, y);
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

    public long getId() {
        return this.id;
    }

    public static long getNewId() {
        return ID_GENERATOR.getAndIncrement();
    }

    static class DrawnTile extends AbstractTile {

        protected DrawnTile(TileLayout tileLayout) {
            super(tileLayout);
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
            this.tileLayout.rotate();
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

        public InsertedTile(TileLayout tileLayout, int x, int y) {
            this(tileLayout, new Tile[NUM_NEIGHBORS], x, y);
        }

        public InsertedTile(TileLayout tileLayout, Tile[] neighbors, int x, int y) {
            super(tileLayout);
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
        public Tile[] neighbors() {
            return neighbors;
        }
    }
}
