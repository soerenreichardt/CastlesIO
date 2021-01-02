package io.castles.core.tile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileLayout {

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    public static final int MIDDLE = 4;

    public static final int NUM_EDGES = 4;
    public static final int NUM_NEIGHBORS = 4;

    private final List<PositionedContent> content;

    private int rotation;
    private final int[] activeRotation;

    public TileLayout(List<PositionedContent> content, int rotation) {
        this(content);
        rotate(rotation);
    }

    public TileLayout(List<PositionedContent> content) {
        this.content = content;
        this.rotation = 0;
        this.activeRotation = new int[]{ LEFT, TOP, RIGHT, BOTTOM };
    }

    public TileContent[] getTileEdges() {
        var tileContents = new TileContent[NUM_EDGES];
        for (PositionedContent positionedContent : content) {
            var tileContent = positionedContent.getContent();
            for (int tileEdge : positionedContent.getTileEdges()) {
                var direction = activeRotation[tileEdge];
                tileContents[direction] = tileContent;
            }
        }
        return tileContents;
    }

    public List<PositionedContent> getContent() {
        return this.content;
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
    public static class PositionedContent {
        TileContent content;
        List<Integer> tileEdges;

        static Builder builder(TileLayout.Builder outerBuilder, TileContent content) {
            return new Builder(outerBuilder, content);
        }

        static class Builder {
            private final TileLayout.Builder outerBuilder;
            private final TileContent content;
            private final List<Integer> tileEdges;

            Builder(TileLayout.Builder outerBuilder, TileContent content) {
                this.outerBuilder = outerBuilder;
                this.content = content;
                this.tileEdges = new ArrayList<>();
            }

            public TileLayout.Builder connectedOnEdges(int... edges) {
                for (int edge : edges) {
                    this.tileEdges.add(edge);
                }
                return outerBuilder.withPositionedContent(new PositionedContent(content, tileEdges));
            }
        }
    }

    static class Builder {
        private final List<PositionedContent> tileLayout;
        private final Set<Integer> seenEdges;

        Builder() {
            this.tileLayout = new ArrayList<>();
            this.seenEdges = new HashSet<>();
        }

        public PositionedContent.Builder withContent(TileContent tileContent) {
            return PositionedContent.builder(this, tileContent);
        }

        public Builder withPositionedContent(PositionedContent positionedContent) {
            validateEdges(positionedContent);
            this.tileLayout.add(positionedContent);
            return this;
        }

        public TileLayout build() {
            return new TileLayout(tileLayout);
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
