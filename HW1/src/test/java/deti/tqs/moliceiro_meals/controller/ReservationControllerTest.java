package deti.tqs.moliceiro_meals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateReservation() throws Exception {
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Reservation mockReservation = new Reservation("John Doe", "john@example.com", "123456789", 4, LocalDateTime.now(), "No special requests", restaurant);

        when(reservationService.createReservation(any(Reservation.class), eq(1L))).thenReturn(mockReservation);

        mockMvc.perform(post("/api/reservations?restaurantId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockReservation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.customerEmail").value("john@example.com"))
                .andExpect(jsonPath("$.customerPhone").value("123456789"))
                .andExpect(jsonPath("$.partySize").value(4))
                .andExpect(jsonPath("$.specialRequests").value("No special requests"));
    }

    @Test
    void testGetReservationById() throws Exception {
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Reservation mockReservation = new Reservation("John Doe", "john@example.com", "123456789", 4, LocalDateTime.now(), "No special requests", restaurant);

        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(mockReservation));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.customerEmail").value("john@example.com"))
                .andExpect(jsonPath("$.customerPhone").value("123456789"))
                .andExpect(jsonPath("$.partySize").value(4))
                .andExpect(jsonPath("$.specialRequests").value("No special requests"));
    }

    @Test
    void testGetReservationByIdNotFound() throws Exception {
        when(reservationService.getReservationById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteReservation() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).deleteReservation(1L);
    }
}