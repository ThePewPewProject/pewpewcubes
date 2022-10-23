package de.kleiner3.lasertag.util;

import java.time.Duration;

public class DurationUtils {
    public static String toString(Duration d) {
        var s = d.getSeconds();
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }
}
