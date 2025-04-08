package deti.tqs.moliceiro_meals.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.WeatherService;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.RestaurantService;

@Controller
public class HomeFrontendController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "Aveiro") String location, Model model) {
        int days = 3; // Number of days to fetch
        List<WeatherData> forecasts = weatherService.getForecastForNextDays(location, days);
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        model.addAttribute("forecasts", forecasts);
        model.addAttribute("restaurants", restaurants);
        return "pages/index";
    }
}