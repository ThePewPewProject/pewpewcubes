package de.pewpewproject.lasertag.common.util;

/**
 * Utility class for string operations
 *
 * @author Étienne Muser
 */
public class StringUtil {
    public static boolean stringEndsWithList(String path, String[] endings) {
        for (var ending : endings) {
            if (path.endsWith(ending)) {
                return true;
            }
        }

        return false;
    }

    public static String[] splitAtNthChar(String string, char character, int n) {
        var idx = -1;

        for(int i = 0; i < n; i++) {
            if (idx + 1 >= string.length()) {
                return new String[] { string, "" };
            }

            idx = string.indexOf(character, idx + 1);
        }

        return new String[] {string.substring(0, idx), string.substring(idx + 1)};
    }
}
