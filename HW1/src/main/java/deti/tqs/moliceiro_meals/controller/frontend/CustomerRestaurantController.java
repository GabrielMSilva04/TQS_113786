package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer")
public class CustomerRestaurantController {

    private final RestaurantService restaurantService;
    private final MenuService menuService;

    public CustomerRestaurantController(RestaurantService restaurantService, MenuService menuService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @GetMapping
    public String home(Model model) {
        // Add featured restaurants and other welcome page data
        model.addAttribute("featuredRestaurants", restaurantService.getFeaturedRestaurants());
        model.addAttribute("pageTitle", "Welcome - Moliceiro Meals");
        return "pages/customer/home";
    }

    @GetMapping("/restaurants")
    public String getRestaurants(Model model) {
        try {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            
            // Create maps for restaurant data instead of adding attributes to Restaurant
            Map<Long, Boolean> hasMenuToday = new HashMap<>();
            Map<Long, List<Menu>> upcomingMenus = new HashMap<>();
            
            // Populate the maps
            LocalDate today = LocalDate.now();
            for (Restaurant restaurant : restaurants) {
                Long id = restaurant.getId();
                
                // Check if restaurant has menu for today
                try {
                    Menu todayMenu = menuService.getMenuByDate(id, today);
                    hasMenuToday.put(id, true);
                } catch (Exception e) {
                    hasMenuToday.put(id, false);
                }
                
                // Get upcoming menus
                try {
                    List<Menu> menus = menuService.getMenusForUpcomingDays(id, 7);
                    upcomingMenus.put(id, menus);
                } catch (Exception e) {
                    upcomingMenus.put(id, new ArrayList<>());
                }
            }
            
            // Add to model
            model.addAttribute("restaurants", restaurants);
            model.addAttribute("hasMenuToday", hasMenuToday);
            model.addAttribute("upcomingMenus", upcomingMenus);
            model.addAttribute("pageTitle", "Restaurants - Moliceiro Meals");
            
            return "pages/customer/restaurants";
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            model.addAttribute("errorMessage", "Could not load restaurants: " + e.getMessage());
            return "pages/customer/restaurants"; // Return the same view, but with error message
        }
    }

    @GetMapping("/restaurant/{id}")
    public String getRestaurant(@PathVariable Long id, Model model) {
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            return "redirect:/customer/restaurants";
        }
        
        Restaurant restaurant = restaurantOpt.get();
        
        // Add breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("label", "Restaurants", "url", "/customer/restaurants"));
        breadcrumbs.add(Map.of("label", restaurant.getName(), "url", null));
        
        // Get today's menu safely
        LocalDate today = LocalDate.now();
        List<Menu> todaysMenus = new ArrayList<>();
        try {
            Menu todayMenu = menuService.getMenuByDate(id, today);
            todaysMenus.add(todayMenu);
        } catch (Exception e) {
            // No menu for today, leave the list empty
        }
        
        // Get upcoming menus safely
        List<Menu> upcomingMenus = new ArrayList<>();
        try {
            upcomingMenus = menuService.getMenusForUpcomingDays(id, 7);
            // Remove today's menu from upcoming if it exists
            upcomingMenus = upcomingMenus.stream()
                .filter(menu -> !menu.getDate().equals(today))
                .collect(Collectors.toList());
        } catch (Exception e) {
            // No upcoming menus
        }
        
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("pageTitle", restaurant.getName() + " - Moliceiro Meals");
        model.addAttribute("todaysMenus", todaysMenus);
        model.addAttribute("upcomingMenus", upcomingMenus);
        model.addAttribute("showReservationBtn", true);
        
        return "pages/customer/restaurant-details";
    }
}