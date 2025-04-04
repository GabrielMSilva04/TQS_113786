package deti.tqs.moliceiro_meals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import deti.tqs.moliceiro_meals.controller.api.MealsController;
import deti.tqs.moliceiro_meals.model.Meal;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.MealService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealsController.class)
public class MealsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealService mealService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetMealsByRestaurant() throws Exception {
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Meal meal1 = new Meal("Steak", "Delicious grilled steak", new BigDecimal("15.99"), restaurant);
        Meal meal2 = new Meal("Salad", "Fresh garden salad", new BigDecimal("10.99"), restaurant);

        when(mealService.getMealsByRestaurant(restaurantId)).thenReturn(List.of(meal1, meal2));

        mockMvc.perform(get("/api/meals/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Steak"))
                .andExpect(jsonPath("$[1].name").value("Salad"));

        verify(mealService, times(1)).getMealsByRestaurant(restaurantId);
    }

    @Test
    void testAddMeal() throws Exception {
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Meal newMeal = new Meal("Pasta", "Creamy pasta", new BigDecimal("12.99"), restaurant);

        when(mealService.addMeal(any(Meal.class))).thenReturn(newMeal);

        mockMvc.perform(post("/api/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMeal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pasta"))
                .andExpect(jsonPath("$.description").value("Creamy pasta"))
                .andExpect(jsonPath("$.price").value(12.99));

        verify(mealService, times(1)).addMeal(any(Meal.class));
    }

    @Test
    void testUpdateMeal() throws Exception {
        Long mealId = 1L;
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Meal updatedMeal = new Meal("Salad", "Fresh garden salad", new BigDecimal("10.99"), restaurant);

        when(mealService.updateMeal(eq(mealId), any(Meal.class))).thenReturn(updatedMeal);

        mockMvc.perform(put("/api/meals/{mealId}", mealId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMeal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salad"))
                .andExpect(jsonPath("$.description").value("Fresh garden salad"))
                .andExpect(jsonPath("$.price").value(10.99));

        verify(mealService, times(1)).updateMeal(eq(mealId), any(Meal.class));
    }

    @Test
    void testDeleteMeal() throws Exception {
        Long mealId = 1L;

        doNothing().when(mealService).deleteMeal(mealId);

        mockMvc.perform(delete("/api/meals/{mealId}", mealId))
                .andExpect(status().isOk());

        verify(mealService, times(1)).deleteMeal(mealId);
    }
}
