package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {
    
    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    private final RestaurantRepository restaurantRepository;
    
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
    
    public List<Restaurant> getAllRestaurants() {
        logger.info("Fetching all restaurants");
        return restaurantRepository.findAll();
    }
    
    public Optional<Restaurant> getRestaurantById(Long id) {
        logger.info("Fetching restaurant with ID: {}", id);
        return restaurantRepository.findById(id);
    }
    
    public List<Restaurant> searchRestaurantsByName(String name) {
        logger.info("Searching restaurants by name: {}", name);
        return restaurantRepository.findByName(name);
    }
    
    public List<Restaurant> searchRestaurantsByLocation(String location) {
        logger.info("Searching restaurants by location: {}", location);
        return restaurantRepository.findByLocation(location);
    }
    
    public Restaurant saveRestaurant(Restaurant restaurant) {
        logger.info("Saving new restaurant: {}", restaurant.getName());
        return restaurantRepository.save(restaurant);
    }
    
    public void deleteRestaurant(Long id) {
        logger.info("Deleting restaurant with ID: {}", id);
        restaurantRepository.deleteById(id);
    }
}