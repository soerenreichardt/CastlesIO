package io.castles.core.tile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static io.castles.core.tile.TileContent.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TileContentTest {

    @Test
    void shouldGetContentsById() {
        assertThat(TileContent.getById(0)).isEqualTo(GRAS);
        assertThat(TileContent.getById(1)).isEqualTo(CASTLE);
        assertThat(TileContent.getById(2)).isEqualTo(STREET);
        assertThat(TileContent.getById(3)).isEqualTo(CHURCH);
        assertThat(TileContent.getById(4)).isEqualTo(SHARED);
    }

    @Test
    void shouldMergeContents() {
        assertThat(merge(GRAS, CASTLE)).isEqualTo(GRAS_AND_CASTLE.getId());
        assertThat(merge(GRAS, STREET)).isEqualTo(GRAS_AND_STREET.getId());
    }

    @Test
    void shouldDoNothingWhenMeringIdenticalContents() {
        assertThat(merge(GRAS, GRAS)).isEqualTo(GRAS.getId());
        assertThat(merge(CHURCH, CHURCH)).isEqualTo(CHURCH.getId());
    }

    static Stream<Arguments> matchingContents() {
        return Stream.of(
                Arguments.of(GRAS, GRAS),
                Arguments.of(CASTLE, CASTLE),
                Arguments.of(STREET, STREET),
                Arguments.of(CHURCH, CHURCH),
                Arguments.of(GRAS, GRAS_AND_CASTLE),
                Arguments.of(GRAS, GRAS_AND_STREET),
                Arguments.of(CASTLE, GRAS_AND_CASTLE),
                Arguments.of(STREET, GRAS_AND_STREET)
        );
    }

    @ParameterizedTest
    @MethodSource("matchingContents")
    void shouldMatchOtherTiles(TileContent lhs, TileContent rhs) {
        assertThat(lhs.matches(rhs)).isTrue();
    }

    static Stream<Arguments> nonMatchingContents() {
        return Stream.of(
                Arguments.of(GRAS, CASTLE),
                Arguments.of(GRAS, STREET),
                Arguments.of(GRAS, CHURCH),
                Arguments.of(CASTLE, CHURCH),
                Arguments.of(STREET, GRAS_AND_CASTLE),
                Arguments.of(CHURCH, GRAS_AND_CASTLE),
                Arguments.of(CASTLE, GRAS_AND_STREET),
                Arguments.of(CHURCH, GRAS_AND_STREET)
        );
    }

    @ParameterizedTest
    @MethodSource("nonMatchingContents")
    void shouldNotMatchOtherTiles(TileContent lhs, TileContent rhs) {
        assertThat(lhs.matches(rhs)).isFalse();
    }

    @Test
    void disconnectedShouldMatchCastleOnly() {
        assertThat(DISCONNECTED.matches(CASTLE)).isTrue();
        Arrays.stream(TileContent.values())
                .filter(content -> content != CASTLE)
                .forEach(content -> assertThat(DISCONNECTED.matches(content)).isFalse());
    }
}
