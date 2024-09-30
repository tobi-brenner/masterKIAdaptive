//package de.htwg.filehandler.storage;
//
//import de.htwg.filehandler.filehandlers.FileHandler;
//import io.quarkus.logging.Log;
//import jakarta.enterprise.context.ApplicationScoped;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@ApplicationScoped
//public class LocalFileStorage implements FileStorage {
//    private final Path rootLocation = Paths.get("uploaded-files");
//
//    public LocalFileStorage() {
//        try {
//            Files.createDirectories(rootLocation);
//        } catch (IOException e) {
//            throw new RuntimeException("Could not initialize storage", e);
//        }
//    }
//
//    public void store(File file, String originalFileName) throws IOException {
//        String extension = getExtension(originalFileName);
//        Files.copy(file.toPath(), this.rootLocation.resolve(originalFileName));
//    }
//
//    private String getExtension(String fileName) {
//        int dotIndex = fileName.lastIndexOf('.');
//        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
//            return fileName.substring(dotIndex + 1);
//        }
//        return null;
//    }
//
//    /**
//     * @param fileName
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public InputStream readFile(String fileName) throws Exception {
//        Path file = rootLocation.resolve(fileName);
//        if (!Files.exists(file)) {
//            throw new Exception("File not found: " + fileName);
//        }
//        return new FileInputStream(file.toFile());
//    }
//
//    /**
//     * @param data
//     * @param fileName
//     * @throws Exception
//     */
//    @Override
//    public void saveFile(InputStream data, String fileName) throws Exception {
//
//
//    }
//}

package de.htwg.filehandler.storage;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@ApplicationScoped
public class LocalFileStorage implements FileStorage {
    private final Path rootLocation = Paths.get("uploaded-files");

    public LocalFileStorage() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public String store(File file, String originalFileName) throws IOException {
        String fileName = resolveFileName(originalFileName);
        Path targetLocation = this.rootLocation.resolve(fileName);
        Files.copy(file.toPath(), targetLocation);
        return targetLocation.toString();
    }

    private String resolveFileName(String originalFileName) throws IOException {
        Path targetLocation = this.rootLocation.resolve(originalFileName);
        if (Files.exists(targetLocation)) {
            String uniqueFileName = appendUniqueIdentifier(originalFileName);
            return uniqueFileName;
        }
        return originalFileName;
    }

    private String appendUniqueIdentifier(String fileName) {
        String uniqueIdentifier = UUID.randomUUID().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName + "_" + uniqueIdentifier;
        } else {
            return fileName.substring(0, dotIndex) + "_" + uniqueIdentifier + fileName.substring(dotIndex);
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }

    @Override
    public InputStream readFile(String fileName) throws Exception {
        Path file = rootLocation.resolve(fileName);
        if (!Files.exists(file)) {
            throw new Exception("File not found: " + fileName);
        }
        return new FileInputStream(file.toFile());
    }

    @Override
    public void saveFile(InputStream data, String fileName) throws Exception {
        Path targetLocation = this.rootLocation.resolve(fileName);
        Files.copy(data, targetLocation);
    }
}
