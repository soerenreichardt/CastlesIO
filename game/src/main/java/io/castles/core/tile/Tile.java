package io.castles.core.tile;

import io.castles.game.IdentifiableObject;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.TestOnly;

import java.util.Random;
import java.util.UUID;

import static io.castles.core.tile.TileLayout.BOTTOM;
import static io.castles.core.tile.TileLayout.LEFT;
import static io.castles.core.tile.TileLayout.NUM_EDGES;
import static io.castles.core.tile.TileLayout.NUM_NEIGHBORS;
import static io.castles.core.tile.TileLayout.RIGHT;
import static io.castles.core.tile.TileLayout.TOP;
import static io.castles.core.tile.TileUtil.oppositeDirection;

@EqualsAndHashCode(callSuper = true)
public class Tile extends IdentifiableObject {

    private AbstractTile delegate;

    public static Tile drawRandom() {
        Random rng = new Random();
        TileLayout.Builder builder = TileLayout.builder();
        for (int direction = 0; direction < NUM_EDGES; direction++) {
            TileContent tileContent = TileContent.getById(rng.nextInt(TileContent.values().length));
            builder.withContent(tileContent).connectedOnEdges(direction);
        }
        return new Tile(builder.build());
    }

    @TestOnly
    public static Tile drawStatic(TileContent content) {
        var tileLayout = TileLayout.builder()
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
        var tileLayout = TileLayout.builder()
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

    private Tile(TileLayout tileLayout) {
        this.delegate = new DrawnTile(tileLayout);
    }

    /**
     * This constructor should only be used to construct
     * a Tile from a TileDTO.
     */
    public Tile(UUID id, TileLayout tileLayout) {
        super(id);
        this.delegate = new DrawnTile(tileLayout);
    }

    public Tile[] getNeighbors() {
        return delegate.neighbors();
    }

    public TileContent[] getTileEdges() {
        return delegate.getTileEdges();
    }

    public TileLayout getTileLayout() {
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
