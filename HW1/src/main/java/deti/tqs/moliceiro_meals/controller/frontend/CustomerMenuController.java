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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerMenuController {

    private final MenuService menuService;
    private final RestaurantService restaurantService;

    public CustomerMenuController(MenuService menuService, RestaurantService restaurantService) {
        this.menuService = menuService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/menus")
    public String getTodaysMenus(Model model) {
        LocalDate today = LocalDate.now();
        
        // Add breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("label", "Today's Menus", "url", null));
        
        // Get all restaurants
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        
        // Get today's menu for each restaurant
        List<Menu> allTodaysMenus = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            try {
                Menu menu = menuService.getMenuByDate(restaurant.getId(), today);
                allTodaysMenus.add(menu);
            } catch (RuntimeException e) {
                // No menu for this restaurant today, skip
            }
        }
        
        model.addAttribute("menus", allTodaysMenus);
        model.addAttribute("date", today);
        model.addAttribute("pageTitle", "Today's Menus - Moliceiro Meals");
        model.addAttribute("breadcrumbs", breadcrumbs);
        
        return "pages/customer/todays-menus";
    }

    @GetMapping("/menus/{id}")
    public String getMenuDetails(@PathVariable Long id, Model model) {
        Menu menu = menuService.getMenuById(id)
            .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        
        // Add breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("label", "Restaurants", "url", "/customer/restaurants"));
        breadcrumbs.add(Map.of("label", menu.getRestaurant().getName(), 
                             "url", "/customer/restaurant/" + menu.getRestaurant().getId()));
        breadcrumbs.add(Map.of("label", menu.getName(), "url", null));
        
        model.addAttribute("menu", menu);
        model.addAttribute("restaurant", menu.getRestaurant());
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("pageTitle", menu.getName() + " - " + menu.getRestaurant().getName());
        
        return "pages/customer/menu-details";
    }
}