package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.repository.ReservationRepository;
import deti.tqs.moliceiro_meals.repository.RestaurantRepository;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;

    public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository) {
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Reservation createReservation(Reservation reservation, Long restaurantId) {
        logger.info("Creating reservation for restaurant ID: {}", restaurantId);

        // Fetch the restaurant by ID
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant with ID " + restaurantId + " not found"));

        // Associate the reservation with the restaurant
        reservation.setRestaurant(restaurant);

        // Generate a unique token for the reservation
        reservation.setToken(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Save the reservation
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> getReservationById(Long id) {
        logger.info("Fetching reservation with ID: {}", id);
        return reservationRepository.findById(id);
    }

    public Optional<Reservation> getReservationByCode(String code) {
        return reservationRepository.findByToken(code);
    }

    public void cancelReservation(String code) {
        Reservation reservation = reservationRepository.findByToken(code)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public void deleteReservation(long id) {
        logger.info("Deleting reservation with ID: {}", id);
        reservationRepository.deleteById(id);
    }

    public void verifyReservation(String code) {
        Reservation reservation = reservationRepository.findByToken(code)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Reservation already used");
        }
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByRestaurant(Long restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation updateReservation(Reservation reservation) {
        logger.info("Updating reservation with ID: {}", reservation.getId());
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public List<Reservation> getReservationsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return reservationRepository.findByReservationTimeBetween(startOfDay, endOfDay);
    }

    public List<Reservation> getRecentReservations(int limit) {
        return reservationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public void confirmReservation(Long id) {
        reservationRepository.findById(id).ifPresent(reservation -> {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        });
    }

    public void staffCancelReservation(Long id) {
        reservationRepository.findById(id).ifPresent(reservation -> {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        });
    }

    public void completeReservation(Long id) {
        reservationRepository.findById(id).ifPresent(reservation -> {
            reservation.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(reservation);
        });
    }
}
