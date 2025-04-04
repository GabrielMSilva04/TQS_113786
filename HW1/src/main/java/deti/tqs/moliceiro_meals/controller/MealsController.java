package deti.tqs.moliceiro_meals.controller;

import deti.tqs.moliceiro_meals.model.Meal;
import deti.tqs.moliceiro_meals.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealsController {

    @Autowired
    private MealService mealService;

    @GetMapping("/{restaurantId}")
    public List<Meal> getMealsByRestaurant(@PathVariable Long restaurantId) {
        return mealService.getMealsByRestaurant(restaurantId);
    }

    @PostMapping
    public Meal addMeal(@RequestBody Meal meal) {
        return mealService.addMeal(meal);
    }
}