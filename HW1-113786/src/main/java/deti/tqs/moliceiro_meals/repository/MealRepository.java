package deti.tqs.moliceiro_meals.repository;

import deti.tqs.moliceiro_meals.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByRestaurantId(Long restaurantId);
}