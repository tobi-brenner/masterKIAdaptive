package de.htwg.filehandler.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FileUploadResourceTest {

    @Test
    void uploadFile() {
//        given()
//                .when().post("files/upload")
//                .then()
//                .statusCode(200)
//                .body(is("File uploaded successfully"));
    }

    @Test
    void uploadFiles() {
    }
}