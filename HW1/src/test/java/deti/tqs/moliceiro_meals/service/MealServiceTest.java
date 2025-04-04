package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.Meal;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.repository.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateMealNotFound() {
        Long mealId = 1L;
        when(mealRepository.findById(mealId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mealService.updateMeal(mealId, new Meal());
        });

        assertEquals("Meal not found", exception.getMessage());
        verify(mealRepository, times(1)).findById(mealId);
    }

    @Test
    void testUpdateMealSuccess() {
        Long mealId = 1L;
        Meal existingMeal = new Meal("Steak", "Delicious grilled steak", new BigDecimal("15.99"), new Restaurant());
        Meal updatedMeal = new Meal("Salad", "Fresh garden salad", new BigDecimal("10.99"), new Restaurant());

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(existingMeal));
        when(mealRepository.save(any(Meal.class))).thenReturn(updatedMeal);

        Meal result = mealService.updateMeal(mealId, updatedMeal);

        assertEquals("Salad", result.getName());
        assertEquals("Fresh garden salad", result.getDescription());
        assertEquals(new BigDecimal("10.99"), result.getPrice());

        ArgumentCaptor<Meal> captor = ArgumentCaptor.forClass(Meal.class);
        verify(mealRepository).save(captor.capture());
        Meal savedMeal = captor.getValue();

        assertEquals("Salad", savedMeal.getName());
        assertEquals("Fresh garden salad", savedMeal.getDescription());
        assertEquals(new BigDecimal("10.99"), savedMeal.getPrice());
    }

    @Test
    void testGetMealsByRestaurant() {
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Meal meal1 = new Meal("Steak", "Delicious grilled steak", new BigDecimal("15.99"), restaurant);
        Meal meal2 = new Meal("Salad", "Fresh garden salad", new BigDecimal("10.99"), restaurant);

        when(mealRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(meal1, meal2));

        List<Meal> meals = mealService.getMealsByRestaurant(restaurantId);

        assertEquals(2, meals.size());
        assertEquals("Steak", meals.get(0).getName());
        assertEquals("Salad", meals.get(1).getName());
        verify(mealRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void testAddMeal() {
        Meal newMeal = new Meal("Pasta", "Creamy pasta", new BigDecimal("12.99"), new Restaurant());
        when(mealRepository.save(newMeal)).thenReturn(newMeal);

        Meal result = mealService.addMeal(newMeal);

        assertEquals("Pasta", result.getName());
        assertEquals("Creamy pasta", result.getDescription());
        assertEquals(new BigDecimal("12.99"), result.getPrice());
        verify(mealRepository, times(1)).save(newMeal);
    }
}
