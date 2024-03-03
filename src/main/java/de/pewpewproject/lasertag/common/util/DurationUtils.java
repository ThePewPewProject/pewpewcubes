package de.pewpewproject.lasertag.common.util;

import java.time.Duration;

/**
 * Utils for durations
 *
 * @author Étienne Muser
 */
public class DurationUtils {
    /**
     * Converts a duration into a string of format h:mm:ss
     * @param d The duration to convert
     * @return The converted string
     */
    public static String toString(Duration d) {
        var s = d.getSeconds();
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

    /**
     * Converts a duration into a string of format mm:ss. If the minutes go over 60 it simply counts up. eg 61, 62,...
     * @param d The duration to convert
     * @return The converted string
     */
    public static String toMinuteString(Duration d) {
        var s = d.getSeconds();
        return String.format("%02d:%02d", s / 60, (s % 60));
    }
}
