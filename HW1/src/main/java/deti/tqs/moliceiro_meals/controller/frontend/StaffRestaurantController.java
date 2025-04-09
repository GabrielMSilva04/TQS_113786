package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff/restaurants")
public class StaffRestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(StaffRestaurantController.class);

    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final ReservationService reservationService;

    public StaffRestaurantController(RestaurantService restaurantService, MenuService menuService, ReservationService reservationService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public String listRestaurants(Model model) {
        logger.info("Displaying all restaurants for staff");
        
        // Get all restaurants
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        
        // Pre-calculate today's menus count for each restaurant
        LocalDate today = LocalDate.now();
        Map<Long, Integer> restaurantTodayMenuCounts = new HashMap<>();
        
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getMenus() != null) {
                long count = restaurant.getMenus().stream()
                    .filter(menu -> menu.getDate() != null && menu.getDate().equals(today))
                    .count();
                restaurantTodayMenuCounts.put(restaurant.getId(), (int) count);
            } else {
                restaurantTodayMenuCounts.put(restaurant.getId(), 0);
            }
        }
        
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("restaurantTodayMenuCounts", restaurantTodayMenuCounts);
        model.addAttribute("pageTitle", "Restaurant Management - Staff Dashboard");
        
        return "pages/staff/restaurants";
    }

    @GetMapping("/{id}")
    public String viewRestaurant(@PathVariable Long id, Model model) {
        logger.info("Viewing details for restaurant ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Restaurant not found");
            return "redirect:/staff/restaurants";
        }
        
        var restaurant = restaurantOpt.get();
        
        // Get today's menus
        LocalDate today = LocalDate.now();
        List<Menu> todayMenus = menuService.getMenusByRestaurantAndDate(id, today);
        
        // Get upcoming menus (excluding today)
        List<Menu> upcomingMenus = menuService.getMenusForUpcomingDays(id, 30)
                .stream()
                .filter(menu -> !menu.getDate().isEqual(today))
                .collect(Collectors.toList());
        
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("todayMenus", todayMenus);
        model.addAttribute("upcomingMenus", upcomingMenus);
        model.addAttribute("reservations", reservationService.getReservationsByRestaurant(id));
        model.addAttribute("pageTitle", restaurant.getName() + " - Staff Dashboard");
        
        return "pages/staff/restaurant-details";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.info("Displaying restaurant creation form");
        model.addAttribute("restaurant", new Restaurant());
        model.addAttribute("pageTitle", "Add New Restaurant - Staff Dashboard");
        return "pages/staff/restaurant-form";
    }
    
    @PostMapping("/create")
    public String createRestaurant(@ModelAttribute Restaurant restaurant, BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("Processing restaurant creation: {}", restaurant.getName());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors in restaurant creation form");
            return "pages/staff/restaurant-form";
        }
        
        try {
            restaurantService.saveRestaurant(restaurant);
            redirectAttributes.addFlashAttribute("successMessage", "Restaurant created successfully");
            return "redirect:/staff/restaurants";
        } catch (Exception e) {
            logger.error("Error creating restaurant: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create restaurant: " + e.getMessage());
            return "redirect:/staff/restaurants/create";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying edit form for restaurant ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant not found");
            return "redirect:/staff/restaurants";
        }
        
        model.addAttribute("restaurant", restaurantOpt.get());
        model.addAttribute("pageTitle", "Edit Restaurant - Staff Dashboard");
        return "pages/staff/restaurant-form";
    }
    
    @PostMapping("/{id}/update")
    public String updateRestaurant(@PathVariable Long id, @ModelAttribute Restaurant restaurant, 
                                  BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("Updating restaurant ID: {}", id);
        
        if (result.hasErrors()) {
            logger.warn("Validation errors in restaurant update form");
            return "pages/staff/restaurant-form";
        }
        
        try {
            // Make sure the ID matches the path variable
            restaurant.setId(id);
            restaurantService.updateRestaurant(restaurant);
            redirectAttributes.addFlashAttribute("successMessage", "Restaurant updated successfully");
            return "redirect:/staff/restaurants/" + id;
        } catch (Exception e) {
            logger.error("Error updating restaurant: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update restaurant: " + e.getMessage());
            return "redirect:/staff/restaurants/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteRestaurant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Deleting restaurant ID: {}", id);
        
        try {
            restaurantService.deleteRestaurant(id);
            redirectAttributes.addFlashAttribute("successMessage", "Restaurant deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting restaurant: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete restaurant: " + e.getMessage());
        }
        
        return "redirect:/staff/restaurants";
    }
    
    // Menu related operations
    @GetMapping("/{id}/menus/create")
    public String showCreateMenuForm(@PathVariable Long id, 
                                    @RequestParam(required = false) String date,
                                    Model model, 
                                    RedirectAttributes redirectAttributes) {
        logger.info("Displaying menu creation form for restaurant ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant not found");
            return "redirect:/staff/restaurants";
        }
        
        Menu newMenu = new Menu();
        newMenu.setRestaurant(restaurantOpt.get());
        
        // Set date if provided
        if (date != null && !date.isEmpty()) {
            try {
                LocalDate menuDate = LocalDate.parse(date);
                newMenu.setDate(menuDate);
            } catch (Exception e) {
                logger.warn("Invalid date format: {}", date);
                // Default to today if date is invalid
                newMenu.setDate(LocalDate.now());
            }
        } else {
            // Default to today if no date provided
            newMenu.setDate(LocalDate.now());
        }
        
        model.addAttribute("menu", newMenu);
        model.addAttribute("restaurant", restaurantOpt.get());
        model.addAttribute("pageTitle", "Create Menu - Staff Dashboard");
        
        return "pages/staff/menu-form";
    }

    @GetMapping("/{id}/menus")
    public String viewRestaurantMenus(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Viewing menus for restaurant ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant not found");
            return "redirect:/staff/restaurants";
        }
        
        var restaurant = restaurantOpt.get();
        
        // Get all menus for this restaurant
        List<Menu> allMenus = menuService.getMenusByRestaurant(id);
        
        // Group menus by month for easier navigation
        Map<String, List<Menu>> menusByMonth = new HashMap<>();
        if (allMenus != null) {
            for (Menu menu : allMenus) {
                if (menu.getDate() != null) {
                    String monthKey = menu.getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                    menusByMonth.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(menu);
                }
            }
        }
        
        // Sort menus within each month by date
        for (List<Menu> monthMenus : menusByMonth.values()) {
            monthMenus.sort(Comparator.comparing(Menu::getDate));
        }
        
        // Get today's menus
        LocalDate today = LocalDate.now();
        List<Menu> todayMenus = menuService.getMenusByRestaurantAndDate(id, today);
        
        // Get upcoming menus (excluding today)
        List<Menu> upcomingMenus = menuService.getMenusForUpcomingDays(id, 30)
                .stream()
                .filter(menu -> !menu.getDate().isEqual(today))
                .collect(Collectors.toList());
        
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("allMenus", allMenus);
        model.addAttribute("menusByMonth", menusByMonth);
        model.addAttribute("todayMenus", todayMenus);
        model.addAttribute("upcomingMenus", upcomingMenus);
        model.addAttribute("pageTitle", restaurant.getName() + " - Menu Management");
        
        return "pages/staff/restaurant-menus";
    }
}