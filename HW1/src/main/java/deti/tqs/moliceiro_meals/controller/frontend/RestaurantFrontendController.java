package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/restaurants")
public class RestaurantFrontendController {

    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final WeatherService weatherService;

    public RestaurantFrontendController(RestaurantService restaurantService, MenuService menuService, WeatherService weatherService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.weatherService = weatherService;
    }

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

    @GetMapping("/{restaurantId}/upcoming-meals")
    public String getUpcomingMeals(@PathVariable Long restaurantId, @RequestParam(defaultValue = "3") int days, Model model) {
        List<Menu> menus = menuService.getMenusForUpcomingDays(restaurantId, days);
        List<WeatherData> forecasts = weatherService.getForecastForNextDays("Aveiro", days); // Replace "Aveiro" with dynamic location if needed
        model.addAttribute("menus", menus);
        model.addAttribute("forecasts", forecasts);
        return "pages/upcoming-meals";
    }

    @GetMapping("/{restaurantId}/menus")
    public String getMenusWithWeather(@PathVariable Long restaurantId, @RequestParam(defaultValue = "3") int days, Model model) {
        List<Menu> menus = menuService.getMenusForUpcomingDays(restaurantId, days);
        List<WeatherData> forecasts = weatherService.getForecastForNextDays("Aveiro", days); // Replace "Aveiro" with dynamic location if needed
        model.addAttribute("menus", menus);
        model.addAttribute("forecasts", forecasts);
        model.addAttribute("restaurantId", restaurantId);
        return "pages/menus";
    }
}
