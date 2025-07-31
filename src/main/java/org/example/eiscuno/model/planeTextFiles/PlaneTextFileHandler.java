package org.example.eiscuno.model.planeTextFiles;

import java.io.*;

/**
 * Implements the IPlaneTextFileHandler interface to provide basic
 * functionality for reading from and writing to plain text files.

 * Handles single-line text content, useful for storing simple values like the player's name.

 * @author David Taborda Montenegro.
 * @version 3.0
 * @since version 3.0
 * @see IPlaneTextFileHandler
 */
public class PlaneTextFileHandler implements IPlaneTextFileHandler {

    /**
     * Writes the given content to a plain text file.
     * The entire content will be written on a single line, overwriting any existing content.
     *
     * @param filename the name of the file to write to.
     * @param content the string to write into the file.
     * @throws IOException if an error occurs during file operations.
     */
    @Override
    public void write(String filename, String content) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the first line from the given plain text file and returns it as a single-element array.
     * If the file is empty or an error occurs, returns an array with an empty string.
     *
     * @param filename the name of the file to read from.
     * @return a String Array containing one element: the first line of the file.
     */
    @Override
    public String[] read(String filename) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line =  reader.readLine();
            return new String[] { (line != null) ? line : "" };
        } catch (IOException e) {
            e.printStackTrace();
            return new String[] { "" };
        }
    }
}
