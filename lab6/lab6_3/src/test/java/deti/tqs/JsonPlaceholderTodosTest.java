package deti.tqs;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class JsonPlaceholderTodosTest {

    static {
        // Set the base URI for REST Assured
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void whenListAllTodos_thenStatusCode200() {
        given()
            .when()
            .get("/todos")
            .then()
            .statusCode(200);
    }

    @Test
    public void whenQueryTodo4_thenTitleIsEtPorroTempora() {
        given()
            .when()
            .get("/todos/4")
            .then()
            .statusCode(200)
            .body("title", equalTo("et porro tempora"));
    }

    @Test
    public void whenListAllTodos_thenContainsIds198And199() {
        given()
            .when()
            .get("/todos")
            .then()
            .statusCode(200)
            .body("id", hasItems(198, 199));
    }

    @Test
    public void whenListAllTodos_thenResponseTimeIsLessThan2Seconds() {
        given()
            .when()
            .get("/todos")
            .then()
            .statusCode(200)
            .time(lessThan(2000L)); // Response time in milliseconds
    }
}