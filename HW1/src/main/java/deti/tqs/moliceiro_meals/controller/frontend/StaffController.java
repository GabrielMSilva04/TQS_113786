package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/staff1")
public class StaffController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    public StaffController(ReservationService reservationService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String staffDashboard(Model model) {
        // Add stats for the dashboard
        model.addAttribute("todayReservationsCount", reservationService.getReservationsByDate(LocalDate.now()).size());
        model.addAttribute("pendingCount", reservationService.getReservationsByStatus(ReservationStatus.PENDING).size());
        model.addAttribute("restaurantCount", restaurantService.getAllRestaurants().size());
        model.addAttribute("recentReservations", reservationService.getRecentReservations(10));
        return "pages/staff/home";
    }

    @GetMapping("/reservations")
    public String allReservations(Model model) {
        model.addAttribute("pendingReservations", reservationService.getReservationsByStatus(ReservationStatus.PENDING));
        model.addAttribute("confirmedReservations", reservationService.getReservationsByStatus(ReservationStatus.CONFIRMED));
        model.addAttribute("cancelledReservations", reservationService.getReservationsByStatus(ReservationStatus.CANCELLED));
        model.addAttribute("completedReservations", reservationService.getReservationsByStatus(ReservationStatus.COMPLETED));
        return "pages/staff/reservations";
    }

    @GetMapping("/reservations/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        var reservation = reservationService.getReservationById(id);
        if (reservation.isEmpty()) {
            return "redirect:/staff/reservations";
        }
        model.addAttribute("reservation", reservation.get());
        return "pages/staff/reservation-details";
    }

    @PostMapping("/reservations/{id}/confirm")
    public String confirmReservation(@PathVariable Long id) {
        reservationService.confirmReservation(id);
        return "redirect:/staff/reservations/" + id;
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(@PathVariable Long id) {
        reservationService.staffCancelReservation(id);
        return "redirect:/staff/reservations/" + id;
    }

    @PostMapping("/reservations/{id}/complete")
    public String completeReservation(@PathVariable Long id) {
        reservationService.completeReservation(id);
        return "redirect:/staff/reservations/" + id;
    }

    @GetMapping("/restaurants")
    public String manageRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "pages/staff/restaurants";
    }

    // Additional restaurant management methods
}