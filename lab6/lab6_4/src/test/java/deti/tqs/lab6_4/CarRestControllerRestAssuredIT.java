package deti.tqs.lab6_4;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarRestControllerRestAssuredIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        carRepository.deleteAll();
    }

    @Test
    void whenValidInput_thenCreateCar() {
        String carJson = """
            {
                "maker": "Tesla",
                "model": "Model X",
                "category": "LUXURY"
            }
        """;

        given()
            .contentType("application/json")
            .body(carJson)
        .when()
            .post("/api/cars")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("maker", equalTo("Tesla"))
            .body("model", equalTo("Model X"));
    }

    @Test
    void givenCars_whenGetCars_thenStatus200AndReturnList() {
        carRepository.save(new Car("Tesla", "Model X", "LUXURY"));
        carRepository.save(new Car("Toyota", "Prius", "COMPACT"));

        given()
        .when()
            .get("/api/cars")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(2))
            .body("maker", containsInAnyOrder("Tesla", "Toyota"));
    }

    @Test
    void whenGetCarById_thenReturnCar() {
        Car savedCar = carRepository.save(new Car("Tesla", "Model X", "LUXURY"));

        given()
        .when()
            .get("/api/cars/{id}", savedCar.getCarId())
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("maker", equalTo("Tesla"))
            .body("model", equalTo("Model X"));
    }

    @Test
    void whenGetInvalidCarId_thenReturn404() {
        given()
        .when()
            .get("/api/cars/{id}", 999)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}