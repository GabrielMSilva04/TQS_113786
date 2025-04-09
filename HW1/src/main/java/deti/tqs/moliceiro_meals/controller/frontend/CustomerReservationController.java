package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customer/reservation")
public class CustomerReservationController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;

    public CustomerReservationController(
            ReservationService reservationService, 
            RestaurantService restaurantService,
            MenuService menuService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @GetMapping("/create")
    public String createReservation(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long menuId,
            Model model) {
        
        // Add breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("label", "Reservation", "url", null));
        
        if (restaurantId != null) {
            var restaurant = restaurantService.getRestaurantById(restaurantId).orElse(null);
            model.addAttribute("restaurant", restaurant);
            
            if (restaurant != null) {
                breadcrumbs.add(Map.of("label", restaurant.getName(), 
                                     "url", "/customer/restaurant/" + restaurantId));
                
                // If a menu was selected, add it to the model
                if (menuId != null) {
                    // Get actual menu instead of mock
                    Optional<Menu> selectedMenuOpt = menuService.getMenuById(menuId);
                    if (selectedMenuOpt.isPresent()) {
                        Menu selectedMenu = selectedMenuOpt.get();
                        model.addAttribute("selectedMenu", selectedMenu);
                        model.addAttribute("menuName", selectedMenu.getName());
                        breadcrumbs.add(Map.of("label", "Menu: " + selectedMenu.getName(), "url", null));
                    }
                }
            }
        }
        
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("pageTitle", "Make a Reservation - Moliceiro Meals");
        return "pages/customer/create-reservation";
    }

    @PostMapping("/create")
    public String submitReservation(
            @ModelAttribute Reservation reservation, 
            @RequestParam Long restaurantId,
            @RequestParam(required = false) Long menuId, 
            Model model) {
        
        try {
            reservation = reservationService.createReservation(reservation, restaurantId);
            
            // Add breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("label", "Reservations", "url", "/customer/reservations"));
            breadcrumbs.add(Map.of("label", "Confirmation", "url", null));
            
            model.addAttribute("reservation", reservation);
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", "Reservation Confirmed - Moliceiro Meals");
            
            // If menu was selected, add it to the confirmation page
            if (menuId != null) {
                Optional<Menu> menuOpt = menuService.getMenuById(menuId);
                if (menuOpt.isPresent()) {
                    model.addAttribute("selectedMenu", menuOpt.get());
                } else {
                    // Fallback to mock data if menu not found
                    Map<String, Object> mockMenu = getMockMenuById(menuId);
                    model.addAttribute("selectedMenu", mockMenu);
                }
            }
            
            return "pages/customer/reservation-confirmation";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId).orElse(null));
            
            // Re-add menu if it was selected
            if (menuId != null) {
                Optional<Menu> menuOpt = menuService.getMenuById(menuId);
                if (menuOpt.isPresent()) {
                    Menu selectedMenu = menuOpt.get();
                    model.addAttribute("selectedMenu", selectedMenu);
                    model.addAttribute("menuName", selectedMenu.getName());
                } else {
                    // Fallback to mock data if menu not found
                    Map<String, Object> mockMenu = getMockMenuById(menuId);
                    model.addAttribute("selectedMenu", mockMenu);
                    model.addAttribute("menuName", mockMenu.get("name"));
                }
            }
            
            return "pages/customer/create-reservation";
        }
    }

    @GetMapping("s")
    public String viewReservations(Model model) {
        // Add breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(Map.of("label", "My Reservations", "url", null));
        
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("pageTitle", "My Reservations - Moliceiro Meals");
        return "pages/customer/view-reservations";
    }

    @GetMapping("/{code}")
    public String viewReservation(@PathVariable String code, Model model) {
        try {
            var reservation = reservationService.getReservationByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            
            // Add breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of("label", "My Reservations", "url", "/customer/reservations"));
            breadcrumbs.add(Map.of("label", "Reservation #" + code, "url", null));
            
            model.addAttribute("reservation", reservation);
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", "Reservation Details - Moliceiro Meals");
            return "pages/customer/reservation-details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/customer/view-reservations";
        }
    }

    @PostMapping("/cancel/{code}")
    public String cancelReservation(@PathVariable String code, Model model) {
        try {
            reservationService.cancelReservation(code);
            model.addAttribute("successMessage", "Reservation cancelled successfully");
            return "redirect:/customer/reservations";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/customer/reservation/" + code;
        }
    }
    
    /**
     * Mock method to get a menu by ID
     * Used as a fallback when the real menu can't be found
     */
    private Map<String, Object> getMockMenuById(Long menuId) {
        Map<String, Object> menu = new HashMap<>();
        menu.put("id", menuId);
        menu.put("name", "Special Menu #" + menuId);
        menu.put("description", "Delicious menu with a variety of options");
        menu.put("date", LocalDate.now());
        
        List<Map<String, Object>> items = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", 1000L + j);
            item.put("name", "Item " + (j + 1));
            item.put("description", "Delicious food item");
            item.put("price", 10.99 + j);
            items.add(item);
        }
        menu.put("items", items);
        
        return menu;
    }
}