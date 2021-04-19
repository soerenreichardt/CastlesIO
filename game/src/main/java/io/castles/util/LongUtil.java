package io.castles.util;

public class LongUtil {

    public static long combineInts(int i1, int i2) {
        return (long) i1 << Integer.SIZE | i2;
    }

    public static int[] decomposeInts(long l) {
        var ints = new int[2];
        ints[0] = (int) (l >> Integer.SIZE);
        ints[1] = (int) l;
        return ints;
    }
}
