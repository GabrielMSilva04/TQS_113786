package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.Meal;
import deti.tqs.moliceiro_meals.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    public List<Meal> getMealsByRestaurant(Long restaurantId) {
        return mealRepository.findByRestaurantId(restaurantId);
    }

    public Meal addMeal(Meal meal) {
        return mealRepository.save(meal);
    }
}