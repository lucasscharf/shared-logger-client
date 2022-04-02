package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;


@QuarkusTest
public class LoggerControllerTest {

    @Test
    public void ping_shouldReturnOk() {
        given()
                .when().get("/ping")
                .then()
                .statusCode(200);
    }
}