package de.htwg.filehandler.storage;

import java.io.InputStream;

public interface FileStorage {
    InputStream readFile(String fileName) throws Exception;

    void saveFile(InputStream data, String fileName) throws Exception;
}
