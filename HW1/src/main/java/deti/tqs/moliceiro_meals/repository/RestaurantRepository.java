package deti.tqs.moliceiro_meals.repository;

import deti.tqs.moliceiro_meals.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByName(String name);
    List<Restaurant> findByLocation(String location);
}