package de.htwg.filehandler.service;

import de.htwg.filehandler.filehandlers.FileHandler;
import de.htwg.filehandler.filehandlers.Mp3FileHandler;
import de.htwg.filehandler.filehandlers.PdfFileHandler;
import de.htwg.filehandler.filehandlers.TextFileHandler;
import de.htwg.filehandler.storage.FileStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class FileRetrievalService {

    @Inject
    private FileStorage storage;

    private Map<String, FileHandler> handlers;

    public FileRetrievalService() {
        handlers = new HashMap<>();
        handlers.put("txt", new TextFileHandler());
        handlers.put("pdf", new PdfFileHandler());
        handlers.put("mp3", new Mp3FileHandler());
    }

    private final Path rootLocation = Paths.get("uploaded-files");

    /**
     * Retrieves a single file as a byte array.
     *
     * @param fileName The name of the file to retrieve.
     * @return The file as a byte array.
     * @throws IOException if an I/O error occurs reading the file.
     */
    public byte[] readFile(String fileName) throws IOException {
        Path file = rootLocation.resolve(fileName);
        return Files.readAllBytes(file);
    }

    /**
     * Retrieves all files in the storage directory.
     *
     * @return A list of byte arrays representing the files.
     * @throws IOException if an I/O error occurs reading the files.
     */
    public List<byte[]> readAllFiles() throws IOException {
        List<Path> paths = Files.walk(rootLocation, 1)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        List<byte[]> files = new ArrayList<>();
        for (Path path : paths) {
            files.add(Files.readAllBytes(path));
        }
        return files;
    }


    public String retrieveFile(String fileName) throws Exception {
        String fileExtension = getFileExtension(fileName);
        FileHandler handler = getFileHandler(fileExtension);

        try (InputStream fileStream = storage.readFile(fileName)) {
            return handler.processFileContent(fileStream);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }

    private FileHandler getFileHandler(String extension) throws Exception {
        switch (extension.toLowerCase()) {
            case "txt":
                return new TextFileHandler();
            case "pdf":
                return new PdfFileHandler();
            case "mp3":
                return new Mp3FileHandler();
            default:
                throw new UnsupportedOperationException("Unsupported file type: " + extension);
        }
    }
}

