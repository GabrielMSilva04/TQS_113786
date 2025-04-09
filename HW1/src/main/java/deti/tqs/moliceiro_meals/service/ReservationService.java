package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.repository.ReservationRepository;
import deti.tqs.moliceiro_meals.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;

    public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository) {
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Reservation createReservation(Reservation reservation, Long restaurantId) {
        logger.info("Creating reservation for restaurant ID: {}", restaurantId);

        // Find the restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant with ID " + restaurantId + " not found"));

        // Set the restaurant
        reservation.setRestaurant(restaurant);

        // Set default values
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        }

        // Generate a unique reservation token/code
        if (reservation.getToken() == null) {
            String token = generateUniqueToken();
            reservation.setToken(token);
        }

        // Set creation time
        reservation.setCreatedAt(LocalDateTime.now());

        // Save and return
        return reservationRepository.save(reservation);
    }

    private String generateUniqueToken() {
        // Generate a random 8-character token
        String token;
        boolean tokenExists;

        do {
            token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            tokenExists = reservationRepository.findByToken(token).isPresent();
        } while (tokenExists);

        return token;
    }

    public Optional<Reservation> getReservationById(Long id) {
        logger.info("Fetching reservation with ID: {}", id);
        return reservationRepository.findById(id);
    }

    public Optional<Reservation> getReservationByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        
        // Trim whitespace and convert to uppercase for consistent lookup
        String normalizedCode = code.trim().toUpperCase();
        logger.info("Finding reservation with normalized code: {}", normalizedCode);
        
        // Try exact match first
        Optional<Reservation> result = reservationRepository.findByToken(normalizedCode);
        
        if (result.isPresent()) {
            return result;
        }
        
        // If not found, try case-insensitive search if your database supports it
        // You may need to add this method to your repository
        return reservationRepository.findByTokenIgnoreCase(normalizedCode);
    }

    @Transactional
    public Reservation cancelReservation(String code) {
        logger.info("Attempting to cancel reservation with code: {}", code);
        
        Reservation reservation = reservationRepository.findByToken(code)
                .orElseThrow(() -> {
                    logger.error("Reservation not found with code: {}", code);
                    return new IllegalArgumentException("Reservation not found with code: " + code);
                });

        logger.info("Found reservation, current status: {}", reservation.getStatus());
        
        // Only allow cancellation of pending or confirmed reservations
        if (reservation.getStatus() != ReservationStatus.PENDING && 
            reservation.getStatus() != ReservationStatus.CONFIRMED) {
            logger.error("Cannot cancel reservation with status: {}", reservation.getStatus());
            throw new IllegalStateException("Cannot cancel reservation with status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        logger.info("Reservation status updated to CANCELLED");
        
        return reservationRepository.save(reservation);
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

    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByCustomerEmail(email);
    }
}
