package deti.tqs.lab3_2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    private MockMvc mvc;

    @Mock
    private CarManagerService service;

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car("Tesla", "Model X", "LUXURY");
        car.setCarId(1L);
        
        CarController carController = new CarController(service);
        mvc = MockMvcBuilders.standaloneSetup(carController).build();
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
    
    // NEW TEST: Find replacement car
    @Test
    void whenFindReplacement_thenReturnReplacementCar() throws Exception {
        Car replacementCar = new Car("BMW", "7 Series", "LUXURY");
        replacementCar.setCarId(2L);
        
        when(service.findReplacement(1L)).thenReturn(Optional.of(replacementCar));

        mvc.perform(
                get("/api/cars/1/replacement").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maker", is("BMW")))
                .andExpect(jsonPath("$.model", is("7 Series")));

        verify(service, times(1)).findReplacement(1L);
    }
    
    // NEW TEST: Find replacement car - no replacement available
    @Test
    void whenFindReplacementNotAvailable_thenReturn404() throws Exception {
        when(service.findReplacement(1L)).thenReturn(Optional.empty());

        mvc.perform(
                get("/api/cars/1/replacement").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).findReplacement(1L);
    }
    
    // NEW TEST: Update car availability
    @Test
    void whenUpdateAvailability_thenReturnUpdatedCar() throws Exception {
        Car updatedCar = new Car("Tesla", "Model X", "LUXURY");
        updatedCar.setCarId(1L);
        updatedCar.setAvailable(true);
        
        when(service.getCarDetails(1L)).thenReturn(Optional.of(car));
        when(service.save(any())).thenReturn(updatedCar);

        mvc.perform(
                put("/api/cars/1/availability").contentType(MediaType.APPLICATION_JSON)
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)));

        verify(service, times(1)).getCarDetails(1L);
        verify(service, times(1)).save(any());
    }
    
    // NEW TEST: Update car availability - car not found
    @Test
    void whenUpdateAvailabilityOfNonExistentCar_thenReturn404() throws Exception {
        when(service.getCarDetails(99L)).thenReturn(Optional.empty());

        mvc.perform(
                put("/api/cars/99/availability").contentType(MediaType.APPLICATION_JSON)
                        .param("available", "true"))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getCarDetails(99L);
        verify(service, never()).save(any());
    }
    
    // NEW TEST: Delete car
    @Test
    void whenDeleteCar_thenReturn204() throws Exception {
        when(service.getCarDetails(1L)).thenReturn(Optional.of(car));
        doNothing().when(service).deleteCar(1L);

        mvc.perform(
                delete("/api/cars/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).getCarDetails(1L);
        verify(service, times(1)).deleteCar(1L);
    }
    
    // NEW TEST: Delete car - car not found
    @Test
    void whenDeleteNonExistentCar_thenReturn404() throws Exception {
        when(service.getCarDetails(99L)).thenReturn(Optional.empty());

        mvc.perform(
                delete("/api/cars/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getCarDetails(99L);
        verify(service, never()).deleteCar(anyLong());
    }
}
