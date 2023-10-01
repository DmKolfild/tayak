package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileManager {
    private final File file;
    public List<String> fileLines;

    public FileManager(String path) throws FileNotFoundException {
        file = new File(path);
        if (!file.exists())
            throw new FileNotFoundException("File not found at path " + path + " =))");
    }

    public void readFileByLines() throws FileNotFoundException {
        if (!file.exists())
            throw new FileNotFoundException("File not found at path " + file.getAbsolutePath());

        try {
            fileLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.println("Fatal ERROR!!!");
        }
    }

    public List<String> getFileLines() {
        return fileLines;
    }
}
