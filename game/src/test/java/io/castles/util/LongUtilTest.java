package io.castles.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LongUtilTest {

    @Test
    void shouldEncodeAndDecodeInts() {
        var longValue = LongUtil.combineInts(42, 1337);
        var ints = LongUtil.decomposeInts(longValue);
        assertThat(ints[0]).isEqualTo(42);
        assertThat(ints[1]).isEqualTo(1337);
    }

}