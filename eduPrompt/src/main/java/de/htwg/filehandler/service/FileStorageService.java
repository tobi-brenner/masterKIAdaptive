//package de.htwg.filehandler.service;
//
//import de.htwg.filehandler.storage.LocalFileStorage;
//import io.quarkus.logging.Log;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//@ApplicationScoped
//public class FileStorageService {
//
//    @Inject
//    LocalFileStorage storage;
//
//    private static final String STORAGE_DIR = "uploaded-files";
//
//    public String storeFile(File file, String fileName) throws IOException {
//        Path targetLocation = Paths.get(STORAGE_DIR).resolve(fileName);
//        Files.createDirectories(targetLocation.getParent()); // Ensure directories exist
//        Files.move(file.toPath(), targetLocation);
//        return targetLocation.toString();
//    }
//
//    public List<String> storeFiles(List<File> files, List<String> fileNames) throws IOException {
//        List<String> storedFilePaths = new ArrayList<>();
//        for (int i = 0; i < files.size(); i++) {
//            String storedFilePath = storeFile(files.get(i), fileNames.get(i));
//            storedFilePaths.add(storedFilePath);
//        }
//        return storedFilePaths;
//    }
//
////    public void storeFile(File file, String fileName) throws IOException {
////        storage.store(file, fileName);
////    }
////
////    public void storeFiles(List<File> files, List<String> fileNames) throws IOException {
////        for (int i = 0; i < files.size(); i++) {
////            storeFile(files.get(i), fileNames.get(i));
////        }
////    }
//}


package de.htwg.filehandler.service;

import de.htwg.filehandler.storage.LocalFileStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FileStorageService {

    @Inject
    LocalFileStorage storage;

    public String storeFile(File file, String fileName) throws IOException {
        return storage.store(file, fileName);
    }

    public List<String> storeFiles(List<File> files, List<String> fileNames) throws IOException {
        List<String> storedFilePaths = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String storedFilePath = storeFile(files.get(i), fileNames.get(i));
            storedFilePaths.add(storedFilePath);
        }
        return storedFilePaths;
    }
}

