package deti.tqs.moliceiro_meals.repository;

import deti.tqs.moliceiro_meals.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByRestaurantId(Long restaurantId);
    List<Menu> findByRestaurantIdAndDate(Long restaurantId, LocalDate date);
    List<Menu> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Menu> findByRestaurantIdAndDateBetween(Long restaurantId, LocalDate startDate, LocalDate endDate);
}