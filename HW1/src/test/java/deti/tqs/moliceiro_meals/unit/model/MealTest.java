package deti.tqs.moliceiro_meals.unit.model;

import deti.tqs.moliceiro_meals.model.Meal;
import deti.tqs.moliceiro_meals.model.Restaurant;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MealTest {

    @Test
    void testConstructorAndGetters() {
        Restaurant restaurant = new Restaurant();
        Meal meal = new Meal("Steak", "Delicious grilled steak", new BigDecimal("15.99"), restaurant);

        assertEquals("Steak", meal.getName());
        assertEquals("Delicious grilled steak", meal.getDescription());
        assertEquals(new BigDecimal("15.99"), meal.getPrice());
        assertEquals(restaurant, meal.getRestaurant());
    }

    @Test
    void testSetters() {
        Restaurant restaurant = new Restaurant();
        Meal meal = new Meal();

        meal.setName("Salad");
        meal.setDescription("Fresh garden salad");
        meal.setPrice(new BigDecimal("5.99"));
        meal.setRestaurant(restaurant);

        assertEquals("Salad", meal.getName());
        assertEquals("Fresh garden salad", meal.getDescription());
        assertEquals(new BigDecimal("5.99"), meal.getPrice());
        assertEquals(restaurant, meal.getRestaurant());
    }
}