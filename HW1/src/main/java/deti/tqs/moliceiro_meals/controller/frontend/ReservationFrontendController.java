package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationFrontendController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/select-restaurant")
    public String selectRestaurant(Model model) {
        model.addAttribute("pageTitle", "Select Restaurant - Moliceiro Meals");
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "pages/select-restaurant";
    }

    @GetMapping
    public String reservationForm(@RequestParam Long restaurantId, Model model) {
        model.addAttribute("pageTitle", "Make a Reservation - Moliceiro Meals");
        model.addAttribute("restaurantId", restaurantId);
        return "pages/reservation";
    }

    @PostMapping
    public String submitReservation(@ModelAttribute Reservation reservation, @RequestParam Long restaurantId, Model model) {
        try {
            reservationService.createReservation(reservation, restaurantId);
            model.addAttribute("successMessage", "Reservation created successfully!");
            model.addAttribute("reservationCode", reservation.getToken());
            return "redirect:/reservation/select-restaurant";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Make a Reservation - Moliceiro Meals");
            return "pages/reservation";
        }
    }

    @GetMapping("/{code}")
    public String getReservationDetails(@PathVariable String code, Model model) {
        Reservation reservation = reservationService.getReservationByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        model.addAttribute("reservation", reservation);
        return "pages/reservation-details";
    }

    @PostMapping("/{code}/cancel")
    public String cancelReservation(@PathVariable String code) {
        // Cancel the reservation
        reservationService.cancelReservation(code);

        // Fetch the reservation to get the associated restaurant ID
        Reservation reservation = reservationService.getReservationByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Redirect to the restaurant's reservations page
        return "redirect:/reservation/restaurant/" + reservation.getRestaurant().getId();
    }

    @PostMapping("/{code}/verify")
    public String verifyReservation(@PathVariable String code) {
        reservationService.verifyReservation(code);
        return "redirect:/reservation/restaurant/" + code;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public String getReservationsByRestaurant(@PathVariable Long restaurantId, Model model) {
        List<Reservation> reservations = reservationService.getReservationsByRestaurant(restaurantId);
        model.addAttribute("reservations", reservations);
        model.addAttribute("restaurantId", restaurantId);
        return "pages/reservations";
    }

    @GetMapping("/list")
    public String getAllReservations(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();

        // Group reservations by status
        List<Reservation> pendingReservations = reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.PENDING)
                .toList();
        List<Reservation> confirmedReservations = reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED)
                .toList();
        List<Reservation> cancelledReservations = reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CANCELLED)
                .toList();
        List<Reservation> completedReservations = reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.COMPLETED)
                .toList();

        model.addAttribute("pendingReservations", pendingReservations);
        model.addAttribute("activeReservations", confirmedReservations);
        model.addAttribute("cancelledReservations", cancelledReservations);
        model.addAttribute("completedReservations", completedReservations);
        model.addAttribute("pageTitle", "Reservations - Moliceiro Meals");
        return "pages/reservations";
    }
}
