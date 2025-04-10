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

    public Meal updateMeal(Long mealId, Meal updatedMeal) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        meal.setName(updatedMeal.getName());
        meal.setDescription(updatedMeal.getDescription());
        meal.setPrice(updatedMeal.getPrice());
        return mealRepository.save(meal);
    }

    public void deleteMeal(Long mealId) {
        mealRepository.deleteById(mealId);
    }
}