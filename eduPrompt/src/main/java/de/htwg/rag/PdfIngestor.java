//package de.htwg.rag;
//
//import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;
//
//import java.io.File;
//import java.util.List;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.enterprise.event.Observes;
//import jakarta.inject.Inject;
//
//import dev.langchain4j.data.document.Document;
//import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
//import dev.langchain4j.data.document.parser.TextDocumentParser;
//import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
//import dev.langchain4j.model.embedding.EmbeddingModel;
//import dev.langchain4j.store.embedding.EmbeddingStore;
//import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
//import io.quarkus.logging.Log;
//import io.quarkus.runtime.StartupEvent;
//
//@ApplicationScoped
//public class PdfIngestor {
//
//    /**
//     * The embedding store (the database).
//     * The bean is provided by the quarkus-langchain4j-redis extension.
//     */
//    @Inject
//    EmbeddingStore store;
//
//    /**
//     * The embedding model (how the vector of a document is computed).
//     * The bean is provided by the LLM (like openai) extension.
//     */
//    @Inject
//    EmbeddingModel embeddingModel;
//
//    public void ingest(@Observes StartupEvent event) {
//        Log.infof("Ingesting documents...");
////        List<Document> documents = FileSystemDocumentLoader.loadDocuments(new File("src/main/resources/catalog").toPath(),
////                new TextDocumentParser());
//        List<Document> documents = FileSystemDocumentLoader.loadDocuments(new File("src/main/resources/samplefiles").toPath(),
//                new ApachePdfBoxDocumentParser());
//        var ingestor = EmbeddingStoreIngestor.builder()
//                .embeddingStore(store)
//                .embeddingModel(embeddingModel)
//                .documentSplitter(recursive(500, 0))
//                .build();
//        ingestor.ingest(documents);
//        Log.infof("Ingested %d documents.%n", documents.size());
//    }
//}


package de.htwg.rag;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkus.logging.Log;

@ApplicationScoped
public class PdfIngestor {

    @Inject
    EmbeddingStore store;

    @Inject
    EmbeddingModel embeddingModel;

    private final Set<String> ingestedFiles = ConcurrentHashMap.newKeySet();
    private static final String STORAGE_DIR = "uploaded-files"; // Define the storage directory

//    public void ingest(@Observes StartupEvent event) {
//        ingestDirectory("src/main/resources/samplefiles");
//    }

    public void ingestDirectory(String directoryPath) {
        Log.infof("Ingesting documents from %s...", directoryPath);
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(Path.of(directoryPath),
                new ApachePdfBoxDocumentParser());
        var ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(500, 0))
                .build();
        ingestor.ingest(documents);
        Log.info(documents);
        documents.forEach(doc -> ingestedFiles.add(getDocumentSource(doc)));
        Log.infof("Ingested %d documents from %s.", documents.size(), directoryPath);
    }

    public void ingestFiles(List<String> filePaths) {
        Log.infof("Ingesting specific documents: %s...", filePaths);
        List<Document> documents = filePaths.stream()
                .filter(filePath -> !ingestedFiles.contains(filePath))
                .map(filePath -> FileSystemDocumentLoader.loadDocument(Paths.get(STORAGE_DIR).resolve(filePath), new ApachePdfBoxDocumentParser()))
                .collect(Collectors.toList());
        if (!documents.isEmpty()) {
            var ingestor = EmbeddingStoreIngestor.builder()
                    .embeddingStore(store)
                    .embeddingModel(embeddingModel)
                    .documentSplitter(recursive(500, 0))
                    .build();
            ingestor.ingest(documents);
            documents.forEach(doc -> ingestedFiles.add(getDocumentSource(doc)));
            Log.infof("Ingested %d new documents.", documents.size());
        } else {
            Log.info("No new documents to ingest.");
        }
    }

    private String getDocumentSource(Document doc) {
        //        return doc.metadata().getSource();
        // Retrieve the document source from metadata
        String fileName = doc.metadata().get(Document.FILE_NAME);
        if (fileName != null) {
            return fileName;
        } else {
            return doc.metadata().get(Document.ABSOLUTE_DIRECTORY_PATH);
        }
    }
}
