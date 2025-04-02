package deti.tqs.moliceiro_meals.repository;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRestaurantId(Long restaurantId);
    List<Reservation> findByRestaurantIdAndReservationTimeBetween(Long restaurantId, LocalDateTime start, LocalDateTime end);
    List<Reservation> findByRestaurantIdAndStatus(Long restaurantId, ReservationStatus status);
    Optional<Reservation> findByToken(String token);
}