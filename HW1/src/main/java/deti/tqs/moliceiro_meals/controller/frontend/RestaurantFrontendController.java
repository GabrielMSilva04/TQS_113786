package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/restaurants")
public class RestaurantFrontendController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/create")
    public String createRestaurantForm(Model model) {
        model.addAttribute("pageTitle", "Create Restaurant - Moliceiro Meals");
        model.addAttribute("restaurant", new Restaurant());
        return "pages/create-restaurant";
    }

    @PostMapping("/create")
    public String createRestaurant(@ModelAttribute Restaurant restaurant, Model model) {
        restaurantService.saveRestaurant(restaurant);
        return "redirect:/restaurants";
    }

    @GetMapping
    public String listRestaurants(Model model) {
        model.addAttribute("pageTitle", "Restaurants - Moliceiro Meals");
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "pages/restaurants";
    }
}
