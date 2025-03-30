package deti.tqs.lab6_4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.AfterAll;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Testcontainers
class CarRestControllerTemplateIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("carsdb")
            .withUsername("root")
            .withPassword("password")
            .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarRepository carRepository;

    @AfterEach
    void cleanup() {
        carRepository.deleteAll();
    }

    @Test
    void whenValidInput_thenCreateCar() {
        Car tesla = new Car("Tesla", "Model X", "LUXURY");
        ResponseEntity<Car> response = restTemplate
                .postForEntity("/api/cars", tesla, Car.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMaker()).isEqualTo("Tesla");
        assertThat(response.getBody().getModel()).isEqualTo("Model X");
    }

    @Test
    void givenCars_whenGetCars_thenStatus200AndReturnList() {
        createTestCar("Tesla", "Model X", "LUXURY");
        createTestCar("Toyota", "Prius", "COMPACT");

        ResponseEntity<List<Car>> response = restTemplate
                .exchange("/api/cars", HttpMethod.GET, null, new ParameterizedTypeReference<List<Car>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .hasSize(2)
                .extracting(Car::getMaker)
                .containsExactlyInAnyOrder("Tesla", "Toyota");
    }

    @Test
    void whenGetCarById_thenReturnCar() {
        Car savedCar = createTestCar("Tesla", "Model X", "LUXURY");

        ResponseEntity<Car> response = restTemplate
                .getForEntity("/api/cars/" + savedCar.getCarId(), Car.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMaker()).isEqualTo("Tesla");
        assertThat(response.getBody().getModel()).isEqualTo("Model X");
    }

    @Test
    void whenGetInvalidCarId_thenReturn404() {
        ResponseEntity<Car> response = restTemplate
                .getForEntity("/api/cars/999", Car.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Car createTestCar(String maker, String model, String category) {
        Car car = new Car(maker, model, category);
        return carRepository.save(car);
    }

    @AfterAll
    static void cleanupContainer() {
        if (mysqlContainer != null && mysqlContainer.isRunning()) {
            mysqlContainer.stop();
        }
    }
} 