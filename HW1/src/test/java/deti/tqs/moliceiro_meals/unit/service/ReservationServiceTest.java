package deti.tqs.moliceiro_meals.unit.service;

import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.repository.ReservationRepository;
import deti.tqs.moliceiro_meals.repository.RestaurantRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private RestaurantRepository restaurantRepository;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationRepository = Mockito.mock(ReservationRepository.class);
        restaurantRepository = Mockito.mock(RestaurantRepository.class);
        // Inject the mocks into the ReservationService
        reservationService = new ReservationService(reservationRepository, restaurantRepository);
    }

    @Test
    void testCreateReservation() {
        // Mock the restaurant
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        // Create a reservation
        Reservation reservation = new Reservation("John Doe", "john@example.com", "123456789", 4, LocalDateTime.now(), "No special requests", null);
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // Call the service method
        Reservation savedReservation = reservationService.createReservation(reservation, 1L);

        // Assertions
        assertNotNull(savedReservation);
        assertEquals(restaurant, savedReservation.getRestaurant());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void testGetReservationById() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Optional<Reservation> foundReservation = reservationService.getReservationById(1L);

        assertTrue(foundReservation.isPresent());
        assertEquals(1L, foundReservation.get().getId());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetReservationByIdNotFound() {
        Long reservationId = 1L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        Optional<Reservation> result = reservationService.getReservationById(reservationId);

        assertTrue(result.isEmpty());
        verify(reservationRepository, times(1)).findById(reservationId);
    }

    @Test
    void testCreateReservationWithValidRestaurant() {
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Reservation reservation = new Reservation("John Doe", "john@example.com", "123456789", 4, LocalDateTime.now(), "No special requests", null);
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation savedReservation = reservationService.createReservation(reservation, 1L);

        assertNotNull(savedReservation);
        assertEquals(restaurant, savedReservation.getRestaurant());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void testCreateReservationWithInvalidRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        Reservation reservation = new Reservation("John Doe", "john@example.com", "123456789", 4, LocalDateTime.now(), "No special requests", null);

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(reservation, 1L));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any());
    }
}
