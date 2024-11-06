package com.phonecompany.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    /**
     * Reads the content of a file from the specified path as a {@code String} using UTF-8 encoding.
     * This is a convenience method that calls {@link #readFileAsString(String, Charset)}
     * with UTF-8 as the default charset.
     *
     * @param filePath the path to the file to be read
     * @return the content of the file as a trimmed {@code String}
     */
    public static String readFileAsString(String filePath) {
        return readFileAsString(filePath, StandardCharsets.UTF_8);
    }

    /**
     * Reads the content of a file from the specified path as a {@code String} with a specified charset.
     *
     * @param filePath the path to the file to be read
     * @param charset  the character encoding to use for reading the file
     * @return the content of the file as a trimmed {@code String}
     * @throws RuntimeException if an I/O error occurs during file reading
     */
    public static String readFileAsString(String filePath, Charset charset) {
        try {
            return Files.readString(Path.of(filePath), charset).trim();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }
}
