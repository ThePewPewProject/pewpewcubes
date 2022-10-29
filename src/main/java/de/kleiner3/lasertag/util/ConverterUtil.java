package de.kleiner3.lasertag.util;

public class ConverterUtil {
    /**
     * Converts a string to the correct primitive type.
     * Currently only supports int and boolean
     * @param value
     * @return
     */
    public static Object stringToPrimitiveType(String value) {
        // Try to convert to int
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {}

        // Convert to boolean
        return Boolean.parseBoolean(value);
    }
}
