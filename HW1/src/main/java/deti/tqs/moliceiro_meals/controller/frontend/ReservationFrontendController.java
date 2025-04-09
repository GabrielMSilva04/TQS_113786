package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationFrontendController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    public ReservationFrontendController(ReservationService reservationService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
    }

    // CUSTOMER VIEWS

    @GetMapping("/customer")
    public String customerHome() {
        return "pages/customer-reservation-home";
    }

    @GetMapping("/customer/select-restaurant")
    public String selectRestaurant(Model model) {
        model.addAttribute("pageTitle", "Select Restaurant - Moliceiro Meals");
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "pages/select-restaurant";
    }

    @GetMapping("/customer/create")
    public String reservationForm(@RequestParam Long restaurantId, Model model) {
        model.addAttribute("pageTitle", "Make a Reservation - Moliceiro Meals");
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId).orElseThrow());
        return "pages/customer-reservation";
    }

    @PostMapping("/customer/create")
    public String submitReservation(@ModelAttribute Reservation reservation, @RequestParam Long restaurantId, Model model) {
        try {
            reservation = reservationService.createReservation(reservation, restaurantId);
            model.addAttribute("successMessage", "Reservation created successfully!");
            model.addAttribute("reservation", reservation);
            return "pages/reservation-confirmation";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Make a Reservation - Moliceiro Meals");
            model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId).orElseThrow());
            return "pages/customer-reservation";
        }
    }

    @GetMapping("/customer/view/{code}")
    public String viewCustomerReservation(@PathVariable String code, Model model) {
        try {
            Reservation reservation = reservationService.getReservationByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            model.addAttribute("reservation", reservation);
            return "pages/customer-reservation-details";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/customer-reservation-home";
        }
    }

    @PostMapping("/customer/cancel/{code}")
    public String customerCancelReservation(@PathVariable String code, Model model) {
        try {
            reservationService.cancelReservation(code);
            model.addAttribute("successMessage", "Reservation cancelled successfully");
            return "redirect:/reservation/customer";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/customer-reservation-home";
        }
    }

    // STAFF VIEWS

    @GetMapping("/staff")
    public String staffHome(Model model) {
        return "pages/staff-reservation-home";
    }

    @GetMapping("/staff/list")
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
        model.addAttribute("pageTitle", "All Reservations - Staff View");
        return "pages/staff-reservations";
    }

    @GetMapping("/staff/restaurant/{restaurantId}")
    public String getReservationsByRestaurant(@PathVariable Long restaurantId, Model model) {
        List<Reservation> reservations = reservationService.getReservationsByRestaurant(restaurantId);
        
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
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId).orElseThrow());
        model.addAttribute("pageTitle", "Restaurant Reservations - Staff View");
        return "pages/staff-reservations";
    }

    @PostMapping("/staff/confirm/{id}")
    public String staffConfirmReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationService.updateReservation(reservation);
        return "redirect:/reservation/staff/list";
    }

    @PostMapping("/staff/cancel/{id}")
    public String staffCancelReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationService.updateReservation(reservation);
        return "redirect:/reservation/staff/list";
    }

    @PostMapping("/staff/checkin/{id}")
    public String staffCheckInReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationService.updateReservation(reservation);
        return "redirect:/reservation/staff/list";
    }

    @PostMapping("/staff/complete/{id}")
    public String staffCompleteReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationService.updateReservation(reservation);
        return "redirect:/reservation/staff/list";
    }
}
