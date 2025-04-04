package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            return "redirect:/reservation/select-restaurant";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Make a Reservation - Moliceiro Meals");
            return "pages/reservation";
        }
    }
}
