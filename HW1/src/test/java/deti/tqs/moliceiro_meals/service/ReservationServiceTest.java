package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.repository.ReservationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        // Mock the ReservationRepository
        reservationRepository = Mockito.mock(ReservationRepository.class);
        // Inject the mock into the ReservationService
        reservationService = new ReservationService(reservationRepository);
    }

    @Test
    void testCreateReservation() {
        Reservation reservation = new Reservation();
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation savedReservation = reservationService.createReservation(reservation, 1L);

        assertNotNull(savedReservation);
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
}
