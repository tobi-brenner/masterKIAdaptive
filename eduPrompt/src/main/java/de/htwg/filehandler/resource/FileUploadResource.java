package de.htwg.filehandler.resource;

import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.course.model.Course;
import de.htwg.course.services.CourseMaterialService;
import de.htwg.filehandler.service.FileRetrievalService;
import de.htwg.filehandler.service.FileStorageService;
//import de.htwg.openai.tts.TextToSpeech;
import de.htwg.openai.tts.models.TTSRequest;
import de.htwg.openai.tts.models.Voice;
import de.htwg.rag.PdfIngestor;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.PartType;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/files")
public class FileUploadResource {
    @Inject
    FileStorageService fileStorageService;

    @Inject
    FileRetrievalService fileRetrievalService;

    @Inject
    CourseMaterialService courseMaterialService;

    @Inject
    PdfIngestor pdfIngestor;



    @POST
    @Path("/uploads/{courseId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response uploadFiles(@PathParam("courseId") Long courseId, FileUploadsForm input) throws IOException {
        Log.info(input);
        Log.info(input.files);
        Log.info(input.fileNames);
        Log.info(courseId);

        try {
            List<String> fileNameList = Arrays.asList(input.fileNames.split(","));
            List<String> storedFilePaths = fileStorageService.storeFiles(input.files, fileNameList);
            Log.info(fileNameList);
            Log.info(storedFilePaths);

            final java.nio.file.Path basePath = Paths.get("uploaded-files");
            List<String> filePaths = storedFilePaths.stream()
                    .map(filePath -> basePath.relativize(Paths.get(filePath)).toString())
                    .collect(Collectors.toList());

            courseMaterialService.addFilesToCourseMaterial(courseId, filePaths);
            pdfIngestor.ingestFiles(filePaths);


            Course course = Course.findById(courseId);
            CourseGetDTO courseGetDTO = new CourseGetDTO(course);

            return Response.ok(courseGetDTO).build();
        } catch (Exception e) {
            Log.info(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }



    @GET
    @Path("/retrieve/{fileName}")
    public Response getFile(@PathParam("fileName") String fileName) {
        try {
            String content = fileRetrievalService.retrieveFile(fileName);
            return Response.ok(content).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving file: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/download/{fileName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("fileName") String fileName) {
        Log.info("Downloading file...");
        try {
            File file = new File("uploaded-files/" + fileName);
            if (!file.exists()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(file)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving file: " + e.getMessage()).build();
        }
    }


    public static class FileUploadForm {
        @FormParam("file")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public File file;

        @FormParam("fileName")
        @PartType(MediaType.TEXT_PLAIN)
        public String fileName;

    }

    public static class FileUploadsForm {
        @FormParam("fileNames")
        @PartType(MediaType.TEXT_PLAIN)
        public String fileNames;

        @FormParam("files")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public List<File> files;
    }
}
