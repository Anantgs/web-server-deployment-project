package com.example;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class MainTest {

    @BeforeAll
    public static void setUp() {
        // Start the server in a separate thread
        new Thread(() -> Main.main(null)).start();

        // Configure RestAssured
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;

        // Small delay to give Spark time to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        spark.Spark.stop();
    }

    @Test
    public void testHelloEndpoint() {
        given()
        .when()
            .get("/hello")
        .then()
            .statusCode(200)
            .body(equalTo("Hello, World!"));
    }
}
