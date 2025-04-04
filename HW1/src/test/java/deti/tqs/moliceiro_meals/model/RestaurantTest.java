package deti.tqs.moliceiro_meals.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void testConstructorAndGetters() {
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");

        assertEquals("Moliceiro Meals", restaurant.getName());
        assertEquals("Aveiro", restaurant.getLocation());
        assertEquals("A cozy restaurant by the canals", restaurant.getDescription());
        assertEquals("123-456-789", restaurant.getContactInfo());
    }

    @Test
    void testSetters() {
        Restaurant restaurant = new Restaurant();

        restaurant.setName("Ocean Breeze");
        restaurant.setLocation("Porto");
        restaurant.setDescription("A seaside restaurant with fresh seafood");
        restaurant.setContactInfo("987-654-321");

        assertEquals("Ocean Breeze", restaurant.getName());
        assertEquals("Porto", restaurant.getLocation());
        assertEquals("A seaside restaurant with fresh seafood", restaurant.getDescription());
        assertEquals("987-654-321", restaurant.getContactInfo());
    }
}