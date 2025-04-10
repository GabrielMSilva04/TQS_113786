package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationFrontendController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationFrontendController.class);
    
    // Model attribute constants
    private static final String ATTR_PAGE_TITLE = "pageTitle";
    private static final String ATTR_RESTAURANTS = "restaurants";
    private static final String ATTR_RESTAURANT = "restaurant";
    private static final String ATTR_RESTAURANT_ID = "restaurantId";
    private static final String ATTR_RESERVATION = "reservation";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_PENDING_RESERVATIONS = "pendingReservations";
    private static final String ATTR_ACTIVE_RESERVATIONS = "activeReservations";
    private static final String ATTR_CANCELLED_RESERVATIONS = "cancelledReservations";
    private static final String ATTR_COMPLETED_RESERVATIONS = "completedReservations";
    
    // View constants
    private static final String VIEW_CUSTOMER_HOME = "pages/customer-reservation-home";
    private static final String VIEW_SELECT_RESTAURANT = "pages/select-restaurant";
    private static final String VIEW_CUSTOMER_RESERVATION = "pages/customer-reservation";
    private static final String VIEW_RESERVATION_CONFIRMATION = "pages/reservation-confirmation";
    private static final String VIEW_CUSTOMER_RESERVATION_DETAILS = "pages/customer-reservation-details";
    private static final String VIEW_STAFF_HOME = "pages/staff-reservation-home";
    private static final String VIEW_STAFF_RESERVATIONS = "pages/staff-reservations";
    
    // Redirect constants
    private static final String REDIRECT_CUSTOMER_HOME = "redirect:/reservation/customer";
    private static final String REDIRECT_STAFF_RESERVATIONS = "redirect:/staff/reservations";
    
    // Message constants
    private static final String SUCCESS_RESERVATION_CREATED = "Reservation created successfully!";
    private static final String SUCCESS_RESERVATION_CANCELLED = "Reservation cancelled successfully";
    private static final String ERROR_RESERVATION_NOT_FOUND = "Reservation not found";

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    public ReservationFrontendController(ReservationService reservationService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
    }

    // CUSTOMER VIEWS

    @GetMapping("/customer")
    public String customerHome() {
        logger.info("Accessing customer reservation home page");
        return VIEW_CUSTOMER_HOME;
    }

    @GetMapping("/customer/select-restaurant")
    public String selectRestaurant(Model model) {
        logger.info("Accessing restaurant selection page");
        model.addAttribute(ATTR_PAGE_TITLE, "Select Restaurant - Moliceiro Meals");
        model.addAttribute(ATTR_RESTAURANTS, restaurantService.getAllRestaurants());
        return VIEW_SELECT_RESTAURANT;
    }

    @GetMapping("/customer/create")
    public String reservationForm(@RequestParam Long restaurantId, Model model) {
        logger.info("Accessing reservation form for restaurant ID: {}", restaurantId);
        model.addAttribute(ATTR_PAGE_TITLE, "Make a Reservation - Moliceiro Meals");
        model.addAttribute(ATTR_RESTAURANT_ID, restaurantId);
        model.addAttribute(ATTR_RESTAURANT, restaurantService.getRestaurantById(restaurantId).orElseThrow());
        return VIEW_CUSTOMER_RESERVATION;
    }

    @PostMapping("/customer/create")
    public String submitReservation(@ModelAttribute Reservation reservation, @RequestParam Long restaurantId, Model model) {
        logger.info("Submitting reservation for restaurant ID: {}", restaurantId);
        try {
            reservation = reservationService.createReservation(reservation, restaurantId);
            model.addAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_RESERVATION_CREATED);
            model.addAttribute(ATTR_RESERVATION, reservation);
            return VIEW_RESERVATION_CONFIRMATION;
        } catch (IllegalArgumentException e) {
            logger.error("Error creating reservation: {}", e.getMessage());
            model.addAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
            model.addAttribute(ATTR_PAGE_TITLE, "Make a Reservation - Moliceiro Meals");
            model.addAttribute(ATTR_RESTAURANT, restaurantService.getRestaurantById(restaurantId).orElseThrow());
            return VIEW_CUSTOMER_RESERVATION;
        }
    }

    @GetMapping("/customer/view/{code}")
    public String viewCustomerReservation(@PathVariable String code, Model model) {
        logger.info("Viewing reservation details for code: {}", code);
        try {
            Reservation reservation = reservationService.getReservationByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException(ERROR_RESERVATION_NOT_FOUND));
            model.addAttribute(ATTR_RESERVATION, reservation);
            return VIEW_CUSTOMER_RESERVATION_DETAILS;
        } catch (IllegalArgumentException e) {
            logger.error("Error viewing reservation: {}", e.getMessage());
            model.addAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
            return VIEW_CUSTOMER_HOME;
        }
    }

    @PostMapping("/customer/cancel/{code}")
    public String customerCancelReservation(@PathVariable String code, Model model) {
        logger.info("Cancelling reservation with code: {}", code);
        try {
            reservationService.cancelReservation(code);
            model.addAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_RESERVATION_CANCELLED);
            return REDIRECT_CUSTOMER_HOME;
        } catch (IllegalArgumentException e) {
            logger.error("Error cancelling reservation: {}", e.getMessage());
            model.addAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
            return VIEW_CUSTOMER_HOME;
        }
    }

    // STAFF VIEWS

    @GetMapping("/staff")
    public String staffHome(Model model) {
        logger.info("Accessing staff reservation home page");
        return VIEW_STAFF_HOME;
    }

    @GetMapping("/staff/list")
    public String getAllReservations(Model model) {
        logger.info("Listing all reservations for staff");
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

        model.addAttribute(ATTR_PENDING_RESERVATIONS, pendingReservations);
        model.addAttribute(ATTR_ACTIVE_RESERVATIONS, confirmedReservations);
        model.addAttribute(ATTR_CANCELLED_RESERVATIONS, cancelledReservations);
        model.addAttribute(ATTR_COMPLETED_RESERVATIONS, completedReservations);
        model.addAttribute(ATTR_PAGE_TITLE, "All Reservations - Staff View");
        return VIEW_STAFF_RESERVATIONS;
    }

    @GetMapping("/staff/restaurant/{restaurantId}")
    public String getReservationsByRestaurant(@PathVariable Long restaurantId, Model model) {
        logger.info("Listing reservations for restaurant ID: {}", restaurantId);
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

        model.addAttribute(ATTR_PENDING_RESERVATIONS, pendingReservations);
        model.addAttribute(ATTR_ACTIVE_RESERVATIONS, confirmedReservations);
        model.addAttribute(ATTR_CANCELLED_RESERVATIONS, cancelledReservations);
        model.addAttribute(ATTR_COMPLETED_RESERVATIONS, completedReservations);
        model.addAttribute(ATTR_RESTAURANT_ID, restaurantId);
        model.addAttribute(ATTR_RESTAURANT, restaurantService.getRestaurantById(restaurantId).orElseThrow());
        model.addAttribute(ATTR_PAGE_TITLE, "Restaurant Reservations - Staff View");
        return VIEW_STAFF_RESERVATIONS;
    }

    @PostMapping("/staff/confirm/{id}")
    public String staffConfirmReservation(@PathVariable Long id) {
        logger.info("Staff confirming reservation ID: {}", id);
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_RESERVATION_NOT_FOUND));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationService.updateReservation(reservation);
        return REDIRECT_STAFF_RESERVATIONS;
    }

    @PostMapping("/staff/cancel/{id}")
    public String staffCancelReservation(@PathVariable Long id) {
        logger.info("Staff cancelling reservation ID: {}", id);
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_RESERVATION_NOT_FOUND));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationService.updateReservation(reservation);
        return REDIRECT_STAFF_RESERVATIONS;
    }

    @PostMapping("/staff/checkin/{id}")
    public String staffCheckInReservation(@PathVariable Long id) {
        logger.info("Staff checking in reservation ID: {}", id);
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_RESERVATION_NOT_FOUND));
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationService.updateReservation(reservation);
        return REDIRECT_STAFF_RESERVATIONS;
    }

    @PostMapping("/staff/complete/{id}")
    public String staffCompleteReservation(@PathVariable Long id) {
        logger.info("Staff completing reservation ID: {}", id);
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_RESERVATION_NOT_FOUND));
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationService.updateReservation(reservation);
        return REDIRECT_STAFF_RESERVATIONS;
    }
}
