package de.kleiner3.lasertag.util;

import java.io.*;

/**
 * Helper methods for file IO
 *
 * @author Étienne Muser
 */
public class FileIO {
    /**
     * Read whole files contents
     * @param file The file to read
     * @return The files contents
     * @throws IOException By File IO
     */
    public static String readAllFile(File file) throws IOException {
        // Create string builder
        var builder = new StringBuilder();

        // Create input stream
        try (var is = new FileInputStream(file)) {
            // Create buffered reader
            try (var br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                // Read every line
                while((line = br.readLine()) != null) {
                    builder.append(line);
                }
            }
        }

        return builder.toString();
    }

    /**
     * Write a string to a file
     * @param file The file to write to
     * @param content The content to write
     * @throws IOException By File IO
     */
    public static void writeAllFile(File file, String content) throws IOException {
        try (var os = new FileOutputStream(file)) {
            try (var bw = new BufferedWriter(new OutputStreamWriter(os))) {
                bw.write(content);
            }
        }
    }
}
