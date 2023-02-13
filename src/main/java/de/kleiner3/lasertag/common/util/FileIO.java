package de.kleiner3.lasertag.common.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        // Create input stream
        try (var is = new FileInputStream(file)) {
            return readAllFile(is);
        }
    }

    /**
     * Read whole files contents
     * @param is The InputStream of the file to read
     * @return The files contents
     * @throws IOException By File IO
     */
    public static String readAllFile(InputStream is) throws IOException {
        // Create string builder
        var builder = new StringBuilder();

        // Create buffered reader
        try (var br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            // Read every line
            while((line = br.readLine()) != null) {
                builder.append(line);
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

    /**
     * Creates a file and the directories leading to it if necessary
     * @param target The path to the file to be created
     * @return The created file
     * @throws IOException By File IO
     */
    public static File createNewFile(String target) throws IOException {
        // Get path of target
        var targetPath = Paths.get(target);

        // If already exists
        if (Files.exists(targetPath)) {
            return new File(target);
        }

        var idx = target.lastIndexOf(File.separatorChar);

        var path = target.substring(0, idx);

        // Create path
        Files.createDirectories(Paths.get(path));

        // Create file
        return Files.createFile(targetPath).toFile();
    }
}
