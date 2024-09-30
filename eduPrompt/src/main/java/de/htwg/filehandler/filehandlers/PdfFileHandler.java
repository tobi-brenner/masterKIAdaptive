package de.htwg.filehandler.filehandlers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class PdfFileHandler implements FileHandler {

    @Override
    public String processFileContent(InputStream fileStream) throws Exception {
        try (PDDocument document = PDDocument.load(fileStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}
