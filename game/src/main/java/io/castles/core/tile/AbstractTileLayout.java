package io.castles.core.tile;

public abstract class AbstractTileLayout implements TileLayout {

    protected int[] activeRotation;
    private int rotation;

    protected AbstractTileLayout() {
        this.activeRotation = new int[]{ LEFT, TOP, RIGHT, BOTTOM };
        this.rotation = 0;
    }

    protected abstract boolean matchesTileWithAppliedRotation(TileContent[] otherTileContentEdge, int rotatedDirection);

    protected abstract TileContent[] getTileContentEdgeWithAppliedRotation(int rotatedDirection);

    public void rotate() {
        this.rotation = (this.rotation + 1) % NUM_EDGES;

        int last = activeRotation[activeRotation.length - 1];
        for (int i = 0; i < this.activeRotation.length; i++) {
            var temp = activeRotation[i];
            this.activeRotation[i] = last;
            last = temp;
        }
    }

    @Override
    public boolean matches(TileLayout other, int direction) {
        var otherTileContentEdge = other.getTileContentEdge(TileSupport.oppositeDirection(direction));
        return matchesTileWithAppliedRotation(otherTileContentEdge, rotatedDirection(direction));
    }

    @Override
    public TileContent[] getTileContentEdge(int direction) {
        return getTileContentEdgeWithAppliedRotation(rotatedDirection(direction));
    }

    public int getRotation() {
        return this.rotation;
    }

    private int rotatedDirection(int direction) {
        return activeRotation[direction];
    }
}
