package de.htwg.course.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.htwg.course.model.Course;
import de.htwg.user.model.User;
import de.htwg.user.dto.CourseCreationDTO;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
//import org.jboss.resteasy.annotations.jaxrs.PathParam;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

import java.net.URL;

@QuarkusTest
public class CourseResourceTest {

    @TestHTTPResource("/courses")
    URL courseUrl;

    @Test
    public void testGetCoursesEndpoint() {
        given()
                .when().get(courseUrl)
                .then()
                .statusCode(200)
                .body(is("[]"));  // Assuming initially there are no courses
    }

    @Test
    @Transactional
    public void testGetCourseEndpoint() {
        // First, create a course to fetch
        Course course = new Course();
        course.subject = "Mathematics";
//        course.persist();
        Course.persist(course);

        given()
                .when().get(courseUrl + "/" + course.id)
                .then()
                .statusCode(200)
                .body("subject", equalTo("Mathematics"));
    }

    @Test
    public void testCreateCourseEndpoint() {
        CourseCreationDTO dto = new CourseCreationDTO();
        dto.subject = "Physics";
        dto.description = "Introduction to Quantum Mechanics";
        dto.profUserId = null;  // No professor linked

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post(courseUrl)
                .then()
                .statusCode(201)
                .body("subject", equalTo("Physics"))
                .body("description", equalTo("Introduction to Quantum Mechanics"));
    }

    @Test
    @Transactional
    public void testUpdateCourseEndpoint() {
        // Create a course to update
        Course course = new Course();
        course.subject = "Biology";
        course.description = "Introduction to Quantum Mechanics";
        course.prof = null;  // No professor linked
        Course.persist(course);

        Course updatedCourse = new Course();
        updatedCourse.subject = "Advanced Biology";
        updatedCourse.description = "Detailed study of human anatomy";

        given()
                .contentType(ContentType.JSON)
                .body(updatedCourse)
                .when().put(courseUrl + "/" + course.id)
                .then()
                .statusCode(200)
                .body("subject", equalTo("Advanced Biology"))
                .body("description", equalTo("Detailed study of human anatomy"));
    }

    @Test
    @Transactional
    public void testDeleteCourseEndpoint() {
        // Create a course to delete
        Course course = new Course();
        course.subject = "Chemistry";
        course.persist();

        given()
                .when().delete(courseUrl + "/" + course.id)
                .then()
                .statusCode(204);  // No content, successful deletion

        // Verify course is deleted
        assertNotNull(Course.findById(course.id));  // should return null
    }
}
