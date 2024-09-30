package de.htwg.ai.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.ai.dto.CourseLLMDTO;
import de.htwg.ai.services.InitialAssessmentGenerationService;
import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.course.model.Course;
import de.htwg.course.model.Topic;
import de.htwg.course.model.Task;
import de.htwg.course.services.AssessmentService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Path("/initassessment")
public class InitialAssessmentGenerationResource {


    @Inject
    InitialAssessmentGenerationService initialAssessmentGenerationService;
    @Inject
    ObjectMapper objectMapper;

    @Inject
    AssessmentService assessmentService;


    @POST
    @Path("/{courseId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createInitialAssignment(@PathParam("courseId") Long courseId) {
        Log.info("INIT ASSESSMENT CREATION STARTING FOR COURSE " + courseId + "...");

        try {
            // Find the course by ID using Panache
            Course course = Course.findById(courseId);
            if (course == null) {
                Log.error("Course not found for ID: " + courseId);
                return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
            }

            // Access topics directly since we are using Panache
            List<Topic> topics = course.topics;

            // Check if there are at least 3 topics, if not handle the error
            if (topics == null || topics.isEmpty()) {
                Log.error("No topics available for course: " + courseId);
                return Response.status(Response.Status.BAD_REQUEST).entity("No topics available for this course").build();
            }

            // Get the first 3 topics or as many as available
            List<Topic> topicsForAssessment = topics.stream().limit(3).collect(Collectors.toList());

            // Create a string representation of the topics
            String topicsString = topicsForAssessment.stream()
                    .map(Topic::toString)
                    .collect(Collectors.joining("\n"));

            Log.info("Selected Topics for Assessment:");
            Log.info(topicsString);

            // Create a string representation of tasks from the selected topics
            String tasksString = topicsForAssessment.stream()
                    .flatMap(topic -> topic.tasks.stream()) // Access tasks directly using Panache-style access
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));

            Log.info("Tasks for Learning Step:");
            Log.info(tasksString);

            // Create a DTO for LLM call
            CourseLLMDTO courseDTO = new CourseLLMDTO(course);
            String courseJson = objectMapper.writeValueAsString(courseDTO);

            Log.info("COURSE JSON:");
            Log.info(courseJson);

            // Call the LLM service to select the initial assessment tasks
            String result = initialAssessmentGenerationService.createInitialCourseAssessment(
                    "courseInitAssess-" + courseId, courseJson, course.courseSettings.language, topicsString, tasksString);

            Log.info("LLM RESULT:::");
            Log.info(result);

            // Parse the response and add the selected tasks to the assessment
            assessmentService.createAssessmentFromSelectedTasks(result, course);

            Log.info("INIT ASSESSMENT CREATION SUCCESSFUL FOR COURSE " + courseId + "...");
            return Response.status(Response.Status.CREATED).build();

        } catch (Exception e) {
            Log.error("ERROR CREATING INITIAL ASSESSMENT FOR " + courseId + "...");
            Log.error(e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error processing request: " + e.getMessage()).build();
        }
    }

}
