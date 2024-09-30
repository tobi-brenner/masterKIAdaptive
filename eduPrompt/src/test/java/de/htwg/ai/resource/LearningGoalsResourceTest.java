package de.htwg.ai.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htwg.ai.services.LearningGoalsService;
import de.htwg.course.model.Course;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
public class LearningGoalsResourceTest {

    @InjectMock
    LearningGoalsService learningGoalsService;

    @InjectMock
    ObjectMapper objectMapper;

    @Test
    public void testDetermineLearningGoalsCourseNotFound() {
        when(Course.findById(1L)).thenReturn(null);

        given()
                .contentType("application/json")
                .body(new LearningGoalsResource.LearningGoalDTO(1L, "create"))
                .when()
                .post("/learninggoal")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(is("Course not found"));
    }

    @Test
    public void testDetermineLearningGoalsInvalidKeyword() {
        Course course = new Course();
        course.id = 1L;
        course.subject = "Test Subject";
        course.description = "Test Description";

        when(Course.findById(1L)).thenReturn(course);

        given()
                .contentType("application/json")
                .body(new LearningGoalsResource.LearningGoalDTO(1L, "invalid"))
                .when()
                .post("/learninggoal")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(is("Invalid keyword provided"));
    }

    @Test
    public void testDetermineLearningGoalsCreateKeyword() {
        Course course = new Course();
        course.id = 1L;
        course.subject = "Test Subject";
        course.description = "Test Description";

        when(Course.findById(1L)).thenReturn(course);
        when(learningGoalsService.determineTopicsAndGoals(anyString(), anyString(), "de")).thenReturn("Generated Learning Goals");

        given()
                .contentType("application/json")
                .body(new LearningGoalsResource.LearningGoalDTO(1L, "create"))
                .when()
                .post("/learninggoal")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(is("Generated Learning Goals"));
    }

    @Test
    public void testDetermineLearningGoalsFurtherKeyword() {
        Course course = new Course();
        course.id = 1L;
        course.subject = "Test Subject";
        course.description = "Test Description";

        when(Course.findById(1L)).thenReturn(course);
        when(learningGoalsService.determineTopicsAndGoals(anyString(), anyString(), "de")).thenReturn("Generated Additional Learning Goals");

        given()
                .contentType("application/json")
                .body(new LearningGoalsResource.LearningGoalDTO(1L, "further"))
                .when()
                .post("/learninggoal")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(is("Generated Additional Learning Goals"));
    }

    @Test
    public void testDetermineLearningGoalsException() {
        when(Course.findById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        given()
                .contentType("application/json")
                .body(new LearningGoalsResource.LearningGoalDTO(1L, "create"))
                .when()
                .post("/learninggoal")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(is("Error processing request: Unexpected error"));
    }
}
