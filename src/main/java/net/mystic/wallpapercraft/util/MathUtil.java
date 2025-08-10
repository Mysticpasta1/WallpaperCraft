package net.mystic.wallpapercraft.util;

public class MathUtil {

    public static int clamp(final int value, final int min, final int max) {
        if (value < min)
            return min;
        else return Math.min(value, max);
    }

    public static int rollOver(final int value, final int min, final int max) {
        if (value < min)
            return max;
        else if (value > max)
            return min;
        else
            return value;
    }
}
