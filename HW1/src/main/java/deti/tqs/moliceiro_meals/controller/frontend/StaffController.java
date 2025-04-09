package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import deti.tqs.moliceiro_meals.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);
    
    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;

    public StaffController(ReservationService reservationService, RestaurantService restaurantService, MenuService menuService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @GetMapping
    public String staffDashboard(Model model) {
        logger.info("Accessing staff dashboard");
        
        // Add stats for the dashboard
        model.addAttribute("todayReservationsCount", reservationService.getReservationsByDate(LocalDate.now()).size());
        model.addAttribute("pendingCount", reservationService.getReservationsByStatus(ReservationStatus.PENDING).size());
        model.addAttribute("restaurantCount", restaurantService.getAllRestaurants().size());
        model.addAttribute("recentReservations", reservationService.getRecentReservations(10));
        model.addAttribute("pageTitle", "Staff Dashboard - Moliceiro Meals");
        
        return "pages/staff/home";
    }

    @GetMapping("/restaurants")
    public String manageRestaurants(Model model) {
        logger.info("Accessing restaurant management");
        
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("pageTitle", "Restaurant Management - Staff View");
        
        return "pages/staff/restaurants";
    }

    @GetMapping("/restaurants/{id}")
    public String viewRestaurant(@PathVariable Long id, Model model) {
        logger.info("Viewing restaurant details for ID: {}", id);
        
        var restaurantOpt = restaurantService.getRestaurantById(id);
        if (restaurantOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Restaurant not found");
            return "redirect:/staff/restaurants";
        }
        
        var restaurant = restaurantOpt.get();
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reservations", reservationService.getReservationsByRestaurant(id));
        model.addAttribute("pageTitle", restaurant.getName() + " - Staff View");
        
        return "pages/staff/restaurant-details";
    }
}