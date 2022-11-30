package de.kleiner3.lasertag.util;

/**
 * Utils for type conversion
 *
 * @author Étienne Muser
 */
public class ConverterUtil {
    /**
     * Converts a string to the correct primitive type.
     * Currently only supports int and boolean
     * @param value The string to convert
     * @return The converted primitive type
     */
    public static Object stringToPrimitiveType(String value) {
        // Try to convert to int
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {}

        // Convert to boolean
        return Boolean.parseBoolean(value);
    }
}
