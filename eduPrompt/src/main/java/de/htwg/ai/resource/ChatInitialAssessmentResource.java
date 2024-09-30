package de.htwg.ai.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.ai.services.ChatInitialAssessmentService;
import de.htwg.course.model.Course;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/chat/assessment/{courseId}")
public class ChatInitialAssessmentResource {
    @Inject
    ChatInitialAssessmentService chatInitialAssessmentService;


    @Inject
    ObjectMapper objectMapper;

    @POST
    public Response createInitialAssignment(@PathParam("courseId") Long courseId, String answer) {
        try {
            Course course = Course.findById(courseId);
            String courseJson = objectMapper.writeValueAsString(course);
            String updatedCourseJson = courseJson + " ---- newAnswer: " + answer;
            String result = chatInitialAssessmentService.createInitialCourseAssessment(String.valueOf(courseId) + "123", updatedCourseJson);
            Log.info(result);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error processing request: " + e.getMessage()).build();
        }
    }
}
