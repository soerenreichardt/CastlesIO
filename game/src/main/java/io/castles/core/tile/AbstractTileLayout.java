package io.castles.core.tile;

public abstract class AbstractTileLayout<T extends AbstractTileLayout<T>> implements TileLayout<T> {

    protected int[] activeRotation;
    private int rotation;

    protected AbstractTileLayout() {
        this.activeRotation = new int[]{ LEFT, TOP, RIGHT, BOTTOM };
        this.rotation = 0;
    }

    protected abstract boolean matchesTileWithAppliedRotation(T other, int rotatedDirection);

    protected abstract TileContent[] getTileContentEdgeWithAppliedRotation(int rotatedDirection);

    public void rotate() {
        this.rotation = (this.rotation + 1) % NUM_EDGES;
        for (int i = 0; i < this.activeRotation.length; i++) {
            this.activeRotation[i] = (this.activeRotation[i] + 1) % NUM_EDGES;
        }
    }

    @Override
    public boolean matches(T other, int direction) {
        return matchesTileWithAppliedRotation(other, rotatedDirection(direction));
    }

    public int getRotation() {
        return this.rotation;
    }

    TileContent[] getTileContentEdge(int direction) {
        return getTileContentEdgeWithAppliedRotation(rotatedDirection(direction));
    }

    private int rotatedDirection(int direction) {
        return activeRotation[direction];
    }
}
