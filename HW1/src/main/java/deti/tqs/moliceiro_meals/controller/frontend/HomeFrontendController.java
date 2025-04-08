package deti.tqs.moliceiro_meals.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.WeatherService;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.RestaurantService;

@Controller
public class HomeFrontendController {

    private final WeatherService weatherService;
    private final RestaurantService restaurantService;

    public HomeFrontendController(WeatherService weatherService, RestaurantService restaurantService) {
        this.weatherService = weatherService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "Aveiro") String location, Model model) {
        int days = 5; // Number of days to fetch
        List<WeatherData> forecasts = weatherService.getForecastForNextDays(location, days);
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        model.addAttribute("forecasts", forecasts);
        model.addAttribute("restaurants", restaurants);
        return "pages/index";
    }
}