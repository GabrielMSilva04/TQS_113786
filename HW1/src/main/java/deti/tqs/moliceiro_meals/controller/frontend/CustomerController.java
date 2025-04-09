package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import deti.tqs.moliceiro_meals.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final WeatherService weatherService;
    
    public CustomerController(
            RestaurantService restaurantService, 
            MenuService menuService,
            WeatherService weatherService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.weatherService = weatherService;
    }
    
    @GetMapping({"", "/"})
    public String showHomePage(Model model) {
        // Get featured restaurants
        List<Restaurant> featuredRestaurants = restaurantService.getFeaturedRestaurants();
        model.addAttribute("featuredRestaurants", featuredRestaurants);
        
        // Get today's menus
        LocalDate today = LocalDate.now();
        Map<Restaurant, List<Menu>> todaysMenus = menuService.getTodaysMenusByRestaurant();
        model.addAttribute("todaysMenus", todaysMenus);
        
        // Get 5-day weather forecast
        try {
            List<WeatherData> weatherForecast = weatherService.getForecastForNextDaysWithTracking("Aveiro", 5);
            model.addAttribute("weatherForecast", weatherForecast);
        } catch (Exception e) {
            // Log the error but don't let it break the page
            model.addAttribute("weatherForecast", new ArrayList<>());
        }
        
        model.addAttribute("pageTitle", "Welcome - Moliceiro Meals");
        return "pages/customer/home";
    }
    
    @GetMapping("/reservations")
    public String redirectToReservations() {
        return "redirect:/customer/reservation/s";
    }
}