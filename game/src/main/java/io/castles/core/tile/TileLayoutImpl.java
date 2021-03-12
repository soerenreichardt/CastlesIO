package io.castles.core.tile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

import static io.castles.core.tile.TileLayout.*;

@EqualsAndHashCode
public class TileLayoutImpl {

    public static final int NUM_EDGES = 4;
    public static final int NUM_NEIGHBORS = 4;

    private final List<PositionedContent> layout;

    @EqualsAndHashCode.Exclude
    private int rotation;
    @EqualsAndHashCode.Exclude
    private final int[] activeRotation;

    public TileLayoutImpl(List<PositionedContent> layout, int rotation) {
        this(layout);
        rotate(rotation);
    }

    public TileLayoutImpl(List<PositionedContent> layout) {
        this.layout = layout;
        this.rotation = 0;
        this.activeRotation = new int[]{ LEFT, TOP, RIGHT, BOTTOM };
    }

    public TileContent[] getTileEdges() {
        var tileContents = new TileContent[NUM_EDGES];
        for (PositionedContent positionedContent : layout) {
            var tileContent = positionedContent.getContent();
            for (int tileEdge : positionedContent.getTileEdges()) {
                var direction = activeRotation[tileEdge];
                tileContents[direction] = tileContent;
            }
        }
        return tileContents;
    }

    public List<PositionedContent> getLayout() {
        return this.layout;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void rotate(int rotations) {
        for (int i = 0; i < rotations; i++) {
            rotate();
        }
    }

    public void rotate() {
        this.rotation = (this.rotation + 1) % NUM_EDGES;
        for (int i = 0; i < this.activeRotation.length; i++) {
            this.activeRotation[i] = (this.activeRotation[i] + 1) % NUM_EDGES;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class PositionedContent {
        TileContent content;
        List<Integer> tileEdges;

        static Builder builder(TileLayoutImpl.Builder outerBuilder, TileContent content) {
            return new Builder(outerBuilder, content);
        }

        public static class Builder {
            private final TileLayoutImpl.Builder outerBuilder;
            private final TileContent content;
            private final List<Integer> tileEdges;

            Builder(TileLayoutImpl.Builder outerBuilder, TileContent content) {
                this.outerBuilder = outerBuilder;
                this.content = content;
                this.tileEdges = new ArrayList<>();
            }

            public TileLayoutImpl.Builder connectedOnEdges(int... edges) {
                for (int edge : edges) {
                    this.tileEdges.add(edge);
                }
                return outerBuilder.withPositionedContent(new PositionedContent(content, tileEdges));
            }
        }
    }

    public static class Builder {
        private final List<PositionedContent> tileLayout;
        private final Set<Integer> seenEdges;
        private Optional<Integer> rotation;

        Builder() {
            this.tileLayout = new ArrayList<>();
            this.seenEdges = new HashSet<>();
            this.rotation = Optional.empty();
        }

        public PositionedContent.Builder withContent(TileContent tileContent) {
            return PositionedContent.builder(this, tileContent);
        }

        public Builder withPositionedContent(PositionedContent positionedContent) {
            validateEdges(positionedContent);
            this.tileLayout.add(positionedContent);
            return this;
        }

        public Builder withRotation(int rotation) {
            this.rotation = Optional.of(rotation);
            return this;
        }

        public TileLayoutImpl build() {
            if (this.rotation.isPresent()) {
                return new TileLayoutImpl(tileLayout, rotation.get());
            }
            return new TileLayoutImpl(tileLayout);
        }

        private void validateEdges(PositionedContent positionedContent) {
            for (int tileEdge : positionedContent.getTileEdges()) {
                if (seenEdges.contains(tileEdge)) {
                    throw new IllegalArgumentException(String.format("Found multiple tile contents on edge %d where only one is allowed", tileEdge));
                }
                seenEdges.add(tileEdge);
            }
        }
    }
}
