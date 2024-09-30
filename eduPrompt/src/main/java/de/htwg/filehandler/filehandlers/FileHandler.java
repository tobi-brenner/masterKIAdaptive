package de.htwg.filehandler.filehandlers;

import java.io.InputStream;

public interface FileHandler {
    String processFileContent(InputStream fileStream) throws Exception;
}
