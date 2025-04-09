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

    @GetMapping("/restaurants")
    public String getRestaurants(Model model) {
        try {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            
            Map<Long, Boolean> hasMenuToday = new HashMap<>();
            Map<Long, List<Menu>> upcomingMenus = new HashMap<>();
            
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
            e.printStackTrace();
            model.addAttribute("errorMessage", "Could not load restaurants: " + e.getMessage());
            return "pages/customer/restaurants"; // Return the same view, but with error message
        }
    }

    @GetMapping("/restaurant/{id}")
    public String getRestaurant(@PathVariable Long id, Model model) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id)
                .orElse(null);
                
            if (restaurant == null) {
                model.addAttribute("errorMessage", "Restaurant not found");
                return "redirect:/customer/restaurants";
            }
            
            model.addAttribute("restaurant", restaurant);
            
            // Get today's menus
            LocalDate today = LocalDate.now();
            List<Menu> todaysMenus = new ArrayList<>();
            try {
                Menu todayMenu = menuService.getMenuByDate(id, today);
                if (todayMenu != null) {
                    todaysMenus.add(todayMenu);
                }
            } catch (Exception e) {

            }
            model.addAttribute("todaysMenus", todaysMenus);
            
            // Get upcoming menus (excluding today)
            List<Menu> upcomingMenus = new ArrayList<>();
            try {
                upcomingMenus = menuService.getMenusForUpcomingDays(id, 14)
                    .stream()
                    .filter(menu -> !menu.getDate().isEqual(today))
                    .collect(Collectors.toList());
            } catch (Exception e) {

            }
            model.addAttribute("upcomingMenus", upcomingMenus);
            
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            Map<String, String> restaurantsCrumb = new HashMap<>();
            restaurantsCrumb.put("label", "Restaurants");
            restaurantsCrumb.put("url", "/customer/restaurants");
            
            Map<String, String> thisCrumb = new HashMap<>();
            thisCrumb.put("label", restaurant.getName());
            thisCrumb.put("url", null);
            
            breadcrumbs.add(restaurantsCrumb);
            breadcrumbs.add(thisCrumb);
            
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", restaurant.getName() + " - Moliceiro Meals");
            
            return "pages/customer/restaurant-details";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error retrieving restaurant details: " + e.getMessage());
            return "redirect:/customer/restaurants";
        }
    }
}