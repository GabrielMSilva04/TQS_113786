package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Reservation reservation, Long restaurantId) {
        logger.info("Creating reservation for restaurant ID: {}", restaurantId);
        // Add logic to associate the reservation with a restaurant
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> getReservationById(Long id) {
        logger.info("Fetching reservation with ID: {}", id);
        return reservationRepository.findById(id);
    }
}
