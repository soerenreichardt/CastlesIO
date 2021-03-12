package io.castles.core.tile;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.TestOnly;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static io.castles.core.tile.TileLayout.*;
import static io.castles.core.tile.TileLayoutImpl.NUM_EDGES;
import static io.castles.core.tile.TileLayoutImpl.NUM_NEIGHBORS;
import static io.castles.core.tile.TileUtil.oppositeDirection;

@EqualsAndHashCode
public class Tile {

    private static final long RESERVED_IDS = 20;
    private static final AtomicLong ID_GENERATOR = new AtomicLong(RESERVED_IDS);

    private final long id;
    private AbstractTile delegate;

    public static Tile drawRandom() {
        Random rng = new Random();
        TileLayoutImpl.Builder builder = TileLayoutImpl.builder();
        for (int direction = 0; direction < NUM_EDGES; direction++) {
            TileContent tileContent = TileContent.getById(rng.nextInt(TileContent.values().length));
            builder.withContent(tileContent).connectedOnEdges(direction);
        }
        return new Tile(builder.build());
    }

    @TestOnly
    public static Tile drawStatic(TileContent content) {
        var tileLayout = TileLayoutImpl.builder()
                .withContent(content)
                .connectedOnEdges(LEFT, RIGHT, TOP, BOTTOM)
                .build();
        return new Tile(tileLayout);
    }

    @TestOnly
    public static Tile drawSpecific(
            TileContent leftEdgeContent,
            TileContent rightEdgeContent,
            TileContent topEdgeContent,
            TileContent bottomEdgeContent
    ) {
        var tileLayout = TileLayoutImpl.builder()
                .withContent(leftEdgeContent)
                .connectedOnEdges(LEFT)
                .withContent(rightEdgeContent)
                .connectedOnEdges(RIGHT)
                .withContent(topEdgeContent)
                .connectedOnEdges(TOP)
                .withContent(bottomEdgeContent)
                .connectedOnEdges(BOTTOM)
                .build();
        return new Tile(tileLayout);
    }

    private Tile(TileLayoutImpl tileLayout) {
        this(getNewId(), tileLayout);
    }

    /**
     * This constructor should only be used to construct
     * a Tile from a TileDTO.
     */
    public Tile(long id, TileLayoutImpl tileLayout) {
        this.id = id;
        this.delegate = new DrawnTile(tileLayout);
    }

    public Tile[] getNeighbors() {
        return delegate.neighbors();
    }

    public TileContent[] getTileEdges() {
        return delegate.getTileEdges();
    }

    public TileLayoutImpl getTileLayout() {
        return delegate.getTileLayout();
    }

    public boolean matches(Tile other, int direction) {
        if (other == null) {
            return true;
        }
        return delegate.getTileEdges()[direction] == other.getTileEdges()[oppositeDirection(direction)];
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

        protected DrawnTile(TileLayoutImpl tileLayout) {
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

        public InsertedTile(TileLayoutImpl tileLayout, int x, int y) {
            this(tileLayout, new Tile[NUM_NEIGHBORS], x, y);
        }

        public InsertedTile(TileLayoutImpl tileLayout, Tile[] neighbors, int x, int y) {
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
