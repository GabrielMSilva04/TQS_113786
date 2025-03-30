package deti.tqs.lab6_4;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.hamcrest.Matchers.*;

@WebMvcTest(CarController.class)
class CarControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarManagerService carService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Initialize RestAssuredMockMvc with the MockMvc instance
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void whenValidInput_thenCreateCar() {
        Car car = new Car("Tesla", "Model X", "LUXURY");
        Mockito.when(carService.save(any(Car.class))).thenReturn(car);

        RestAssuredMockMvc.given()
            .contentType("application/json")
            .body(car)
        .when()
            .post("/api/cars")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("maker", equalTo("Tesla"))
            .body("model", equalTo("Model X"));
    }

    @Test
    void givenCars_whenGetCars_thenReturnList() {
        Car car1 = new Car("Tesla", "Model X", "LUXURY");
        Car car2 = new Car("Toyota", "Prius", "COMPACT");
        Mockito.when(carService.getAllCars()).thenReturn(Arrays.asList(car1, car2));

        RestAssuredMockMvc.given()
        .when()
            .get("/api/cars")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(2))
            .body("maker", containsInAnyOrder("Tesla", "Toyota"));
    }

    @Test
    void whenGetCarById_thenReturnCar() {
        Car car = new Car("Tesla", "Model X", "LUXURY");
        Mockito.when(carService.getCarDetails(anyLong())).thenReturn(Optional.of(car));

        RestAssuredMockMvc.given()
        .when()
            .get("/api/cars/{id}", 1)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("maker", equalTo("Tesla"))
            .body("model", equalTo("Model X"));
    }

    @Test
    void whenGetInvalidCarId_thenReturn404() {
        Mockito.when(carService.getCarDetails(anyLong())).thenReturn(Optional.empty());

        RestAssuredMockMvc.given()
        .when()
            .get("/api/cars/{id}", 999)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}