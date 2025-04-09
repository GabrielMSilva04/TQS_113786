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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer")
public class CustomerRestaurantController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerRestaurantController.class);
    
    // View constants
    private static final String VIEW_RESTAURANTS = "pages/customer/restaurants";
    private static final String VIEW_RESTAURANT_DETAILS = "pages/customer/restaurant-details";
    
    // Model attribute constants
    private static final String ATTR_RESTAURANTS = "restaurants";
    private static final String ATTR_RESTAURANT = "restaurant";
    private static final String ATTR_HAS_MENU_TODAY = "hasMenuToday";
    private static final String ATTR_UPCOMING_MENUS = "upcomingMenus";
    private static final String ATTR_TODAYS_MENUS = "todaysMenus";
    private static final String ATTR_PAGE_TITLE = "pageTitle";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_BREADCRUMBS = "breadcrumbs";
    
    // Map key constants
    private static final String KEY_LABEL = "label";
    private static final String KEY_URL = "url";
    
    // URL constants
    private static final String URL_CUSTOMER_RESTAURANTS = "/customer/restaurants";

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
                
                // Extract nested try blocks into separate methods
                hasMenuToday.put(id, checkMenuExistsForToday(id, today));
                upcomingMenus.put(id, getUpcomingMenusForRestaurant(id, 7));
            }
            
            // Add to model
            model.addAttribute(ATTR_RESTAURANTS, restaurants);
            model.addAttribute(ATTR_HAS_MENU_TODAY, hasMenuToday);
            model.addAttribute(ATTR_UPCOMING_MENUS, upcomingMenus);
            model.addAttribute(ATTR_PAGE_TITLE, "Restaurants - Moliceiro Meals");
            
            return VIEW_RESTAURANTS;
        } catch (Exception e) {
            logger.error("Error loading restaurants", e);
            model.addAttribute(ATTR_ERROR_MESSAGE, "Could not load restaurants: " + e.getMessage());
            return VIEW_RESTAURANTS; // Return the same view, but with error message
        }
    }
    
    private boolean checkMenuExistsForToday(Long restaurantId, LocalDate date) {
        try {
            Menu todayMenu = menuService.getMenuByDate(restaurantId, date);
            return todayMenu != null;
        } catch (Exception e) {
            logger.warn("Error checking menu for restaurant {}: {}", restaurantId, e.getMessage());
            return false;
        }
    }
    
    private List<Menu> getUpcomingMenusForRestaurant(Long restaurantId, int daysAhead) {
        try {
            return menuService.getMenusForUpcomingDays(restaurantId, daysAhead);
        } catch (Exception e) {
            logger.warn("Error getting upcoming menus for restaurant {}: {}", restaurantId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @GetMapping("/restaurant/{id}")
    public String getRestaurant(@PathVariable Long id, Model model) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id)
                .orElse(null);
                
            if (restaurant == null) {
                model.addAttribute(ATTR_ERROR_MESSAGE, "Restaurant not found");
                return "redirect:" + URL_CUSTOMER_RESTAURANTS;
            }
            
            model.addAttribute(ATTR_RESTAURANT, restaurant);
            
            // Extract nested try blocks into method calls
            model.addAttribute(ATTR_TODAYS_MENUS, getTodaysMenus(id));
            model.addAttribute(ATTR_UPCOMING_MENUS, getUpcomingMenusExcludingToday(id, 14));
            
            List<Map<String, String>> breadcrumbs = createBreadcrumbs(restaurant);
            model.addAttribute(ATTR_BREADCRUMBS, breadcrumbs);
            model.addAttribute(ATTR_PAGE_TITLE, restaurant.getName() + " - Moliceiro Meals");
            
            return VIEW_RESTAURANT_DETAILS;
        } catch (Exception e) {
            logger.error("Error retrieving restaurant details", e);
            model.addAttribute(ATTR_ERROR_MESSAGE, "Error retrieving restaurant details: " + e.getMessage());
            return "redirect:" + URL_CUSTOMER_RESTAURANTS;
        }
    }
    
    private List<Menu> getTodaysMenus(Long restaurantId) {
        List<Menu> todaysMenus = new ArrayList<>();
        try {
            LocalDate today = LocalDate.now();
            Menu todayMenu = menuService.getMenuByDate(restaurantId, today);
            if (todayMenu != null) {
                todaysMenus.add(todayMenu);
            }
        } catch (Exception e) {
            logger.warn("Error getting today's menu for restaurant {}: {}", restaurantId, e.getMessage());
        }
        return todaysMenus;
    }
    
    private List<Menu> getUpcomingMenusExcludingToday(Long restaurantId, int daysAhead) {
        List<Menu> upcomingMenus = new ArrayList<>();
        try {
            LocalDate today = LocalDate.now();
            upcomingMenus = menuService.getMenusForUpcomingDays(restaurantId, daysAhead)
                .stream()
                .filter(menu -> !menu.getDate().isEqual(today))
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("Error getting upcoming menus for restaurant {}: {}", restaurantId, e.getMessage());
        }
        return upcomingMenus;
    }
    
    private List<Map<String, String>> createBreadcrumbs(Restaurant restaurant) {
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> restaurantsCrumb = new HashMap<>();
        restaurantsCrumb.put(KEY_LABEL, "Restaurants");
        restaurantsCrumb.put(KEY_URL, URL_CUSTOMER_RESTAURANTS);
        
        Map<String, String> thisCrumb = new HashMap<>();
        thisCrumb.put(KEY_LABEL, restaurant.getName());
        thisCrumb.put(KEY_URL, null);
        
        breadcrumbs.add(restaurantsCrumb);
        breadcrumbs.add(thisCrumb);
        
        return breadcrumbs;
    }
}