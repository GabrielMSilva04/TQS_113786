package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);
    
    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    public StaffController(ReservationService reservationService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
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
}