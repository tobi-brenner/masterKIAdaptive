package de.htwg.filehandler.filehandlers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TextFileHandler implements FileHandler {
    @Override
    public String processFileContent(InputStream fileStream) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
