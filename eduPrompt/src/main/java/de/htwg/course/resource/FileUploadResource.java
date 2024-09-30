package de.htwg.course.resource;
import de.htwg.course.IngestCourseMaterial;
import de.htwg.course.services.CoursePromptService;
import de.htwg.course.services.PdfService;
import io.smallrye.mutiny.Uni;
//import io.vertx.redis.client.impl.RedisClient;
import io.quarkus.redis.client.RedisClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


//@ApplicationScoped
@Path("/material/upload")
public class FileUploadResource{

    private static final Logger LOG = Logger.getLogger(FileUploadResource.class.getName());



    @Inject
    PdfService pdfService;

    @Inject
    RedisClient redisClient;

    @Inject
    CoursePromptService coursePromptService;



    @Schema(type = SchemaType.STRING, format = "binary")
    public interface UploadItemSchema {
    }


    public static class UploadFormSchema {
        public List<UploadItemSchema> files;
    }


    @Schema(implementation = UploadFormSchema.class)
    public static class MultipartBody {
        @RestForm("files")
        public List<FileUpload> files;
    }
    public class SimpleResponse {
        private String message;

        public SimpleResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "SimpleResponse{message='" + message + '\'' + '}';
        }
    }
}

