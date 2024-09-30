package de.htwg.course.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class PdfService {
    private static final Logger LOG = Logger.getLogger(PdfService.class);

    public String extractTextFromPDF(String filePath) {
        String text = null;
        File file = new File(filePath);

        try (PDDocument document = PDDocument.load(file)) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                text = stripper.getText(document);
                LOG.info("Extracted text: " + text);
            }
        } catch (IOException e) {
            LOG.error("Failed to process PDF", e);
            // Depending on how you want to handle the error, you can throw it, return null, or choose another approach
            throw new RuntimeException("Failed to process PDF", e);
        }
        return text;
    }

    public void createPDF(String content, String filename) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4); // Create a new page of A4 size
            document.addPage(page);

            try (PDPageContentStream contents = new PDPageContentStream(document, page)) {
                contents.beginText();
                contents.setFont(PDType1Font.TIMES_ROMAN, 12); // Set font and size
                contents.newLineAtOffset(100, 750); // Set the position of the first line

                // Assuming `content` doesn't contain newlines and fits on one page
                String[] lines = content.split("\n");
                for (String line : lines) {
                    contents.showText(line);
                    contents.newLineAtOffset(0, -15); // Move to the next line
                }

                contents.endText();
            }

            document.save(filename); // Save the document to a file
        }
    }
}
