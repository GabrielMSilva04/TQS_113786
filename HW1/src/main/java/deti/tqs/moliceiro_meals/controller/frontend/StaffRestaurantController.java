package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.MenuItem;
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

@Controller
@RequestMapping("/staff/restaurants")
public class StaffRestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(StaffRestaurantController.class);
    
    // View constants
    private static final String VIEW_RESTAURANTS = "pages/staff/restaurants";
    private static final String VIEW_RESTAURANT_DETAILS = "pages/staff/restaurant-details";
    private static final String VIEW_RESTAURANT_FORM = "pages/staff/restaurant-form";
    private static final String VIEW_RESTAURANT_MENUS = "pages/staff/restaurant-menus";
    private static final String VIEW_MENU_FORM = "pages/staff/menu-form";
    private static final String VIEW_MENU_ITEM_FORM = "pages/staff/menu-item-form";
    
    // Redirect constants
    private static final String REDIRECT_STAFF_RESTAURANTS = "redirect:/staff/restaurants";
    private static final String REDIRECT_STAFF_RESTAURANT_ID = "redirect:/staff/restaurants/";
    private static final String REDIRECT_STAFF_MENUS = "redirect:/staff/menus/";
    
    // Model attribute constants
    private static final String ATTR_RESTAURANTS = "restaurants";
    private static final String ATTR_RESTAURANT = "restaurant";
    private static final String ATTR_RESTAURANT_TODAY_MENU_COUNTS = "restaurantTodayMenuCounts";
    private static final String ATTR_MENU = "menu";
    private static final String ATTR_TODAY_MENUS = "todayMenus";
    private static final String ATTR_UPCOMING_MENUS = "upcomingMenus";
    private static final String ATTR_ALL_MENUS = "allMenus";
    private static final String ATTR_MENUS_BY_MONTH = "menusByMonth";
    private static final String ATTR_RESERVATIONS = "reservations";
    private static final String ATTR_MENU_ITEM = "menuItem";
    private static final String ATTR_PAGE_TITLE = "pageTitle";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    
    // Success message constants
    private static final String SUCCESS_RESTAURANT_CREATED = "Restaurant created successfully";
    private static final String SUCCESS_RESTAURANT_UPDATED = "Restaurant updated successfully";
    private static final String SUCCESS_RESTAURANT_DELETED = "Restaurant deleted successfully";
    private static final String SUCCESS_MENU_ITEM_ADDED = "Menu item added successfully";
    private static final String SUCCESS_MENU_ITEM_DELETED = "Menu item deleted successfully";
    
    // Error message constants
    private static final String ERROR_RESTAURANT_NOT_FOUND = "Restaurant not found";
    private static final String ERROR_MENU_NOT_FOUND = "Menu not found";
    private static final String ERROR_MENU_ITEM_NOT_FOUND = "Menu item not found";
    private static final String ERROR_MENU_NOT_BELONG_RESTAURANT = "Menu does not belong to this restaurant";
    private static final String ERROR_MENU_ITEM_NOT_BELONG_MENU = "Menu item does not belong to this menu";
    private static final String ERROR_MENU_ITEM_NOT_BELONG_RESTAURANT = "Menu item does not belong to this restaurant";
    private static final String ERROR_CREATE_RESTAURANT = "Failed to create restaurant: ";
    private static final String ERROR_UPDATE_RESTAURANT = "Failed to update restaurant: ";
    private static final String ERROR_DELETE_RESTAURANT = "Failed to delete restaurant: ";
    private static final String ERROR_ADD_MENU_ITEM = "Failed to add menu item: ";
    private static final String ERROR_DELETE_MENU_ITEM = "Failed to delete menu item: ";

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
        
        model.addAttribute(ATTR_RESTAURANTS, restaurants);
        model.addAttribute(ATTR_RESTAURANT_TODAY_MENU_COUNTS, restaurantTodayMenuCounts);
        model.addAttribute(ATTR_PAGE_TITLE, "Restaurant Management - Staff Dashboard");
        
        return VIEW_RESTAURANTS;
    }

    @GetMapping("/{id}")
    public String viewRestaurant(@PathVariable Long id, Model model) {
        logger.info("Viewing details for restaurant ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            model.addAttribute(ATTR_ERROR_MESSAGE, ERROR_RESTAURANT_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        var restaurant = restaurantOpt.get();
        
        // Get today's menus
        LocalDate today = LocalDate.now();
        List<Menu> todayMenus = menuService.getMenusByRestaurantAndDate(id, today);
        
        // Get upcoming menus (excluding today)
        List<Menu> upcomingMenus = menuService.getMenusForUpcomingDays(id, 30)
                .stream()
                .filter(menu -> !menu.getDate().isEqual(today))
                .toList();
        
        model.addAttribute(ATTR_RESTAURANT, restaurant);
        model.addAttribute(ATTR_TODAY_MENUS, todayMenus);
        model.addAttribute(ATTR_UPCOMING_MENUS, upcomingMenus);
        model.addAttribute(ATTR_RESERVATIONS, reservationService.getReservationsByRestaurant(id));
        model.addAttribute(ATTR_PAGE_TITLE, restaurant.getName() + " - Staff Dashboard");
        
        return VIEW_RESTAURANT_DETAILS;
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.info("Displaying restaurant creation form");
        model.addAttribute(ATTR_RESTAURANT, new Restaurant());
        model.addAttribute(ATTR_PAGE_TITLE, "Add New Restaurant - Staff Dashboard");
        return VIEW_RESTAURANT_FORM;
    }
    
    @PostMapping("/create")
    public String createRestaurant(@ModelAttribute Restaurant restaurant, BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("Processing restaurant creation: {}", restaurant.getName());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors in restaurant creation form");
            return VIEW_RESTAURANT_FORM;
        }
        
        try {
            restaurantService.saveRestaurant(restaurant);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_RESTAURANT_CREATED);
            return REDIRECT_STAFF_RESTAURANTS;
        } catch (Exception e) {
            logger.error("Error creating restaurant: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_CREATE_RESTAURANT + e.getMessage());
            return REDIRECT_STAFF_RESTAURANT_ID + "create";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying edit form for restaurant ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_RESTAURANT_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        model.addAttribute(ATTR_RESTAURANT, restaurantOpt.get());
        model.addAttribute(ATTR_PAGE_TITLE, "Edit Restaurant - Staff Dashboard");
        return VIEW_RESTAURANT_FORM;
    }
    
    @PostMapping("/{id}/update")
    public String updateRestaurant(@PathVariable Long id, @ModelAttribute Restaurant restaurant, 
                                  BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("Updating restaurant ID: {}", id);
        
        if (result.hasErrors()) {
            logger.warn("Validation errors in restaurant update form");
            return VIEW_RESTAURANT_FORM;
        }
        
        try {
            // Make sure the ID matches the path variable
            restaurant.setId(id);
            restaurantService.updateRestaurant(restaurant);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_RESTAURANT_UPDATED);
            return REDIRECT_STAFF_RESTAURANT_ID + id;
        } catch (Exception e) {
            logger.error("Error updating restaurant: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_UPDATE_RESTAURANT + e.getMessage());
            return REDIRECT_STAFF_RESTAURANT_ID + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteRestaurant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Deleting restaurant ID: {}", id);
        
        try {
            restaurantService.deleteRestaurant(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_RESTAURANT_DELETED);
        } catch (Exception e) {
            logger.error("Error deleting restaurant: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_DELETE_RESTAURANT + e.getMessage());
        }
        
        return REDIRECT_STAFF_RESTAURANTS;
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
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_RESTAURANT_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
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
        
        model.addAttribute(ATTR_MENU, newMenu);
        model.addAttribute(ATTR_RESTAURANT, restaurantOpt.get());
        model.addAttribute(ATTR_PAGE_TITLE, "Create Menu - Staff Dashboard");
        
        return VIEW_MENU_FORM;
    }

    @GetMapping("/{id}/menus")
    public String viewRestaurantMenus(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Viewing menus for restaurant ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_RESTAURANT_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
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
                .toList();
        
        model.addAttribute(ATTR_RESTAURANT, restaurant);
        model.addAttribute(ATTR_ALL_MENUS, allMenus);
        model.addAttribute(ATTR_MENUS_BY_MONTH, menusByMonth);
        model.addAttribute(ATTR_TODAY_MENUS, todayMenus);
        model.addAttribute(ATTR_UPCOMING_MENUS, upcomingMenus);
        model.addAttribute(ATTR_PAGE_TITLE, restaurant.getName() + " - Menu Management");
        
        return VIEW_RESTAURANT_MENUS;
    }

    @PostMapping("/{restaurantId}/menus/{menuId}/items")
    public String addMenuItemToMenu(@PathVariable Long restaurantId,
                                   @PathVariable Long menuId,
                                   @ModelAttribute MenuItem menuItem,
                                   RedirectAttributes redirectAttributes) {
        logger.info("Adding menu item to menu ID: {} for restaurant ID: {}", menuId, restaurantId);
        
        // Validate restaurant exists
        var restaurantOpt = restaurantService.getRestaurantById(restaurantId);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_RESTAURANT_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        // Validate menu exists
        var menuOpt = menuService.getMenuById(menuId);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANT_ID + restaurantId;
        }
        
        Menu menu = menuOpt.get();
        
        // Validate menu belongs to restaurant
        if (!menu.getRestaurant().getId().equals(restaurantId)) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_BELONG_RESTAURANT);
            return REDIRECT_STAFF_RESTAURANT_ID + restaurantId;
        }
        
        try {
            // Associate the menu item with the menu
            menuItem.setMenu(menu);
            
            // Save the menu item
            menuService.addMenuItem(menuItem);
            
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_ITEM_ADDED);
            return REDIRECT_STAFF_MENUS + menuId;
        } catch (Exception e) {
            logger.error("Error adding menu item: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_ADD_MENU_ITEM + e.getMessage());
            return REDIRECT_STAFF_MENUS + menuId;
        }
    }

    @GetMapping("/{restaurantId}/menus/{menuId}/items/add")
    public String showAddMenuItemForm(@PathVariable Long restaurantId,
                                     @PathVariable Long menuId,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        logger.info("Displaying add menu item form for menu ID: {} of restaurant ID: {}", menuId, restaurantId);
        
        // Validate restaurant exists
        var restaurantOpt = restaurantService.getRestaurantById(restaurantId);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_RESTAURANT_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        // Validate menu exists
        var menuOpt = menuService.getMenuById(menuId);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANT_ID + restaurantId;
        }
        
        Menu menu = menuOpt.get();
        
        // Validate menu belongs to restaurant
        if (!menu.getRestaurant().getId().equals(restaurantId)) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_BELONG_RESTAURANT);
            return REDIRECT_STAFF_RESTAURANT_ID + restaurantId;
        }
        
        // Create a new menu item
        MenuItem menuItem = new MenuItem();
        
        model.addAttribute(ATTR_MENU_ITEM, menuItem);
        model.addAttribute(ATTR_MENU, menu);
        model.addAttribute(ATTR_RESTAURANT, restaurantOpt.get());
        model.addAttribute(ATTR_PAGE_TITLE, "Add Menu Item - Staff Dashboard");
        
        return VIEW_MENU_ITEM_FORM;
    }

    @PostMapping("/{restaurantId}/menus/{menuId}/items/{itemId}/delete")
    public String deleteMenuItem(@PathVariable Long restaurantId,
                               @PathVariable Long menuId,
                               @PathVariable Long itemId,
                               RedirectAttributes redirectAttributes) {
        logger.info("Deleting menu item ID: {} from menu ID: {} of restaurant ID: {}", itemId, menuId, restaurantId);
        
        try {
            // Validate the menu item exists
            var menuItemOpt = menuService.getMenuItemById(itemId);
            if (menuItemOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_ITEM_NOT_FOUND);
                return REDIRECT_STAFF_MENUS + menuId;
            }
            
            // Validate the menu item belongs to the correct menu and restaurant
            MenuItem menuItem = menuItemOpt.get();
            if (menuItem.getMenu() == null || !menuItem.getMenu().getId().equals(menuId)) {
                redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_ITEM_NOT_BELONG_MENU);
                return REDIRECT_STAFF_MENUS + menuId;
            }
            
            if (menuItem.getMenu().getRestaurant() == null || !menuItem.getMenu().getRestaurant().getId().equals(restaurantId)) {
                redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_ITEM_NOT_BELONG_RESTAURANT);
                return REDIRECT_STAFF_RESTAURANT_ID + restaurantId;
            }
            
            // Delete the menu item
            menuService.deleteMenuItem(itemId);
            
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_ITEM_DELETED);
        } catch (Exception e) {
            logger.error("Error deleting menu item: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_DELETE_MENU_ITEM + e.getMessage());
        }
        
        return REDIRECT_STAFF_MENUS + menuId;
    }
}