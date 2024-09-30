package de.htwg.course.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import io.quarkus.logging.Log;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import io.smallrye.mutiny.Uni;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Path("/texts")
public class TextFileUploadResource {

    private static final Logger LOG = Logger.getLogger(FileUploadResource.class);

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> uploadTextFile(@RestForm FileUpload fileUpload) {
//        LOG.info(fileUpload);
//        LOG.info(fileUpload.uploadedFile());
//        LOG.info("hi");
        Log.info("--------");
        Log.info(fileUpload);
        Log.info("--------");


        File uploadedFile = fileUpload.uploadedFile().toFile(); // This returns a java.io.File
        Log.info("--------");
        Log.info(uploadedFile);
        Log.info("--------");
        Path path = (Path) uploadedFile;
//        Path path = (Path) uploadedFile.toPath(); // Convert File to Path
        return Uni.createFrom().deferred(() -> {
            try {
                // Read all lines from the file as a stream and join them into a single string
                String content = Files.lines((java.nio.file.Path) path, StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
                System.out.println("Received content: " + content);  // Process content, e.g., logging
                // Asynchronously delete the file if needed, or handle the file path as required
                Files.deleteIfExists((java.nio.file.Path) path);
                return Uni.createFrom().item(Response.ok("Text file uploaded and processed successfully").build());
            } catch (Exception e) {
                return Uni.createFrom().failure(new WebApplicationException("Error processing text file: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR));
            }
        });


    }
}

