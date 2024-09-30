package de.htwg.filehandler.filehandlers;

import java.io.InputStream;

public class Mp3FileHandler implements FileHandler {

    @Override
    public String processFileContent(InputStream fileStream) throws Exception {
        // Processing logic for MP3 files (e.g., extracting metadata)
        return "MP3 file processing not implemented";
    }
}
