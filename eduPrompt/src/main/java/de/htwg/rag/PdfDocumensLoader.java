package de.htwg.rag;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkiverse.langchain4j.redis.RedisEmbeddingStore;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;

import java.io.IOException;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

public class PdfDocumensLoader {
    private final EmbeddingStoreIngestor ingestor;

    public PdfDocumensLoader(RedisEmbeddingStore embeddingStore, EmbeddingModel embeddingModel) {
        ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(500, 0))
                .build();
    }

    public void onStart(@Observes StartupEvent startupEvent) throws IOException {
//        ingestPdfsFromDirectory("C:/Users/tobia/IdeaProjects/edu/eduPrompt/pdfs");
    }

    void ingestPdfsFromDirectory(String directory) throws IOException {
        var documents = FileSystemDocumentLoader.loadDocuments(directory, new ApachePdfBoxDocumentParser());
        ingestor.ingest(documents);


    }
}
