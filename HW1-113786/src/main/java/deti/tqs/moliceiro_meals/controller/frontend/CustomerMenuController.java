package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer")
public class CustomerMenuController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerMenuController.class);

    private final MenuService menuService;
    private final RestaurantService restaurantService;

    public CustomerMenuController(MenuService menuService, RestaurantService restaurantService) {
        this.menuService = menuService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/menus")
    public String getTodaysMenus(Model model) {
        LocalDate today = LocalDate.now();
        logger.info("Fetching today's menus for {}", today);
        
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> menusCrumb = new HashMap<>();
        menusCrumb.put("label", "Today's Menus");
        menusCrumb.put("url", null);
        
        breadcrumbs.add(menusCrumb);
        
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        
        Map<Restaurant, List<Menu>> menusByRestaurant = new HashMap<>();
        
        // For each restaurant, get today's menus
        for (Restaurant restaurant : restaurants) {
            if (restaurant == null) {
                logger.warn("Null restaurant found in restaurants list");
                continue;
            }
            
            try {
                List<Menu> todaysMenus = menuService.getMenusByRestaurantAndDate(restaurant.getId(), today);
                if (todaysMenus != null && !todaysMenus.isEmpty()) {
                    // Filter out any null menus
                    List<Menu> validMenus = todaysMenus.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                    
                    if (!validMenus.isEmpty()) {
                        for (Menu menu : validMenus) {
                            logger.debug("Found menu: name={}, date={}, items={}", 
                                menu.getName(), 
                                menu.getDate(),
                                menu.getItems() != null ? menu.getItems().size() : "null"
                            );
                        }
                        menusByRestaurant.put(restaurant, validMenus);
                    }
                }
            } catch (Exception e) {
                logger.info("No menu found for restaurant {} on {}: {}", 
                            restaurant.getName(), today, e.getMessage());
            }
        }
        
        logger.info("Found menus for {} restaurants today", menusByRestaurant.size());
        model.addAttribute("menusByRestaurant", menusByRestaurant);
        model.addAttribute("date", today);
        model.addAttribute("today", today);
        model.addAttribute("pageTitle", "Today's Menus - Moliceiro Meals");
        model.addAttribute("breadcrumbs", breadcrumbs);
        
        return "pages/customer/todays-menus";
    }
    
    @GetMapping("/menus/{id}")
    public String getMenuDetails(@PathVariable Long id, Model model) {
        try {
            Menu menu = menuService.getMenuById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
                
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            
            Map<String, String> menusCrumb = new HashMap<>();
            menusCrumb.put("label", "Today's Menus");
            menusCrumb.put("url", "/customer/menus");
            
            Map<String, String> menuDetailsCrumb = new HashMap<>();
            menuDetailsCrumb.put("label", menu.getName());
            menuDetailsCrumb.put("url", null);
            
            breadcrumbs.add(menusCrumb);
            breadcrumbs.add(menuDetailsCrumb);
            
            model.addAttribute("menu", menu);
            model.addAttribute("restaurant", menu.getRestaurant());
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", menu.getName() + " - Moliceiro Meals");
            
            return "pages/customer/menu-details";
        } catch (Exception e) {
            logger.error("Error fetching menu details: {}", e.getMessage());
            model.addAttribute("errorMessage", "Menu not found");
            return "redirect:/customer/menus";
        }
    }
}