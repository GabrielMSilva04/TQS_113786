package deti.tqs.lab6_4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CarManagerService service;

    private Car car;

    @BeforeEach
    public void setUp() {
        car = new Car("Tesla", "Model X", "LUXURY");
        car.setCarId(1L);
    }

    @Test
    void whenPostCar_thenCreateCar() throws Exception {
        when(service.save(any())).thenReturn(car);

        mvc.perform(
                post("/api/cars").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"maker\": \"Tesla\", \"model\": \"Model X\"}")
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maker", is("Tesla")))
                .andExpect(jsonPath("$.model", is("Model X")));

        verify(service, times(1)).save(any());
    }

    @Test
    void givenManyCars_whenGetCars_thenReturnJsonArray() throws Exception {
        Car car2 = new Car("Toyota", "Prius", "COMPACT");
        car2.setCarId(2L);

        when(service.getAllCars()).thenReturn(Arrays.asList(car, car2));

        mvc.perform(
                get("/api/cars").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].maker", is("Tesla")))
                .andExpect(jsonPath("$[1].maker", is("Toyota")));

        verify(service, times(1)).getAllCars();
    }

    @Test
    void whenGetCarById_thenReturnJson() throws Exception {
        when(service.getCarDetails(1L)).thenReturn(Optional.of(car));

        mvc.perform(
                get("/api/cars/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maker", is("Tesla")))
                .andExpect(jsonPath("$.model", is("Model X")));

        verify(service, times(1)).getCarDetails(1L);
    }

    @Test
    void whenGetInvalidCarById_thenReturnNotFound() throws Exception {
        when(service.getCarDetails(99L)).thenReturn(Optional.empty());

        mvc.perform(
                get("/api/cars/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getCarDetails(99L);
    }
}
