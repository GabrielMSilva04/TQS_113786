package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customer/reservation")
public class CustomerReservationController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerReservationController.class);
    
    // Constants for common string literals
    private static final String ATTR_RESERVATION = "reservation";
    private static final String ATTR_RESTAURANTS = "restaurants";
    private static final String ATTR_RESTAURANT = "restaurant";
    private static final String ATTR_SELECTED_MENU = "selectedMenu";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_BREADCRUMBS = "breadcrumbs";
    private static final String ATTR_PAGE_TITLE = "pageTitle";
    private static final String ATTR_RESERVATIONS = "reservations";
    private static final String ATTR_RESERVATION_FORM = "reservationForm";
    private static final String ATTR_RESERVATION_DATE = "reservationDate";
    private static final String ATTR_RESERVATION_TIME = "reservationTime";
    private static final String ATTR_INFO_MESSAGE = "infoMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    
    // Constants for view paths
    private static final String VIEW_CREATE_RESERVATION = "pages/customer/create-reservation";
    private static final String VIEW_RESERVATION_CONFIRMATION = "pages/customer/reservation-confirmation";
    private static final String VIEW_VIEW_RESERVATIONS = "pages/customer/view-reservations";
    private static final String VIEW_RESERVATION_DETAILS = "pages/customer/reservation-details";
    
    // Constants for breadcrumb labels
    private static final String LABEL_RESERVATIONS = "Reservations";
    private static final String LABEL_MY_RESERVATIONS = "My Reservations";
    private static final String LABEL_MAKE_RESERVATION = "Make Reservation";
    private static final String LABEL_CONFIRMATION = "Confirmation";
    
    // Constants for map keys
    private static final String KEY_LABEL = "label";
    private static final String KEY_URL = "url";
    
    // Paths for URLs
    private static final String URL_CUSTOMER_RESERVATIONS = "/customer/reservation/s";
    private static final String URL_CUSTOMER_RESTAURANT = "/customer/restaurant/";
    
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // This prevents Spring from trying to directly convert strings to LocalDateTime
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(null);
            }
        });
    }

    @GetMapping("/create")
    public String createReservation(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long menuId,
            Model model) {
        
        logger.info("Create reservation form requested with restaurantId={}, menuId={}", restaurantId, menuId);
        
        Reservation reservation = new Reservation();
        model.addAttribute(ATTR_RESERVATION, reservation);
        
        model.addAttribute(ATTR_RESTAURANTS, restaurantService.getAllRestaurants());
        
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> reservationCrumb = new HashMap<>();
        reservationCrumb.put(KEY_LABEL, LABEL_MAKE_RESERVATION);
        reservationCrumb.put(KEY_URL, null);
        
        breadcrumbs.add(reservationCrumb);
        
        if (restaurantId != null) {
            logger.info("Fetching restaurant with ID: {}", restaurantId);
            var restaurantOpt = restaurantService.getRestaurantById(restaurantId);
            if (restaurantOpt.isPresent()) {
                Restaurant restaurant = restaurantOpt.get();
                model.addAttribute(ATTR_RESTAURANT, restaurant);
                
                Map<String, String> restaurantCrumb = new HashMap<>();
                restaurantCrumb.put(KEY_LABEL, restaurant.getName());
                restaurantCrumb.put(KEY_URL, URL_CUSTOMER_RESTAURANT + restaurantId);
                breadcrumbs.add(1, restaurantCrumb);
                
                // If a menu was selected, add it to the model
                if (menuId != null) {
                    logger.info("Fetching menu with ID: {}", menuId);
                    Optional<Menu> selectedMenuOpt = menuService.getMenuById(menuId);
                    if (selectedMenuOpt.isPresent()) {
                        Menu selectedMenu = selectedMenuOpt.get();
                        model.addAttribute(ATTR_SELECTED_MENU, selectedMenu);
                        
                        Map<String, String> menuCrumb = new HashMap<>();
                        menuCrumb.put(KEY_LABEL, "Menu: " + selectedMenu.getName());
                        menuCrumb.put(KEY_URL, null);
                        breadcrumbs.add(2, menuCrumb);
                    } else {
                        logger.warn("Menu with ID {} not found", menuId);
                    }
                }
            } else {
                logger.warn("Restaurant with ID {} not found", restaurantId);
                model.addAttribute(ATTR_ERROR_MESSAGE, "Restaurant not found");
            }
        }
        
        model.addAttribute(ATTR_BREADCRUMBS, breadcrumbs);
        model.addAttribute(ATTR_PAGE_TITLE, "Make a Reservation - Moliceiro Meals");
        
        logger.info("Rendering create-reservation view");
        return VIEW_CREATE_RESERVATION;
    }

    @PostMapping("/create")
    public String submitReservation(
            @ModelAttribute(ATTR_RESERVATION_FORM) Reservation reservation,
            @RequestParam Long restaurantId,
            @RequestParam(required = false) Long menuId,
            @RequestParam(required = false) String reservationDate,
            @RequestParam(required = false) String reservationTime,
            Model model) {
        
        try {
            logger.info("Submitting reservation with date: {} and time: {}", reservationDate, reservationTime);
            
            // Validate required fields
            if (reservation.getCustomerName() == null || reservation.getCustomerName().isEmpty()) {
                throw new IllegalArgumentException("Customer name is required");
            }
            if (reservation.getCustomerEmail() == null || reservation.getCustomerEmail().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (reservation.getCustomerPhone() == null || reservation.getCustomerPhone().isEmpty()) {
                throw new IllegalArgumentException("Phone number is required");
            }
            if (reservation.getPartySize() == null) {
                throw new IllegalArgumentException("Party size is required");
            }
            
            // Process date and time
            if (reservationDate != null && !reservationDate.isEmpty() && 
                reservationTime != null && !reservationTime.isEmpty()) {
                
                parseAndSetReservationDateTime(reservation, reservationDate, reservationTime);

            } else {
                throw new IllegalArgumentException("Reservation date and time are required");
            }
            
            // Get the restaurant
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId)
                    .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
            
            // Associate menu if provided
            if (menuId != null) {
                menuService.getMenuById(menuId).ifPresent(menu -> {
                    reservation.setMenu(menu);
                    logger.info("Associated menu: {} with the reservation", menu.getName());
                });
            }
            
            // Set additional fields
            reservation.setRestaurant(restaurant);
            reservation.setStatus(ReservationStatus.PENDING);
            
            // Create the reservation
            Reservation createdReservation = reservationService.createReservation(reservation, restaurantId);
            
            // Add information for the confirmation page
            model.addAttribute(ATTR_RESERVATION, createdReservation);
            model.addAttribute(ATTR_RESTAURANT, restaurant);
            model.addAttribute(ATTR_PAGE_TITLE, "Reservation Confirmed - Moliceiro Meals");
            
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            
            Map<String, String> reservationsCrumb = new HashMap<>();
            reservationsCrumb.put(KEY_LABEL, LABEL_RESERVATIONS);
            reservationsCrumb.put(KEY_URL, URL_CUSTOMER_RESERVATIONS);
            breadcrumbs.add(reservationsCrumb);
            
            Map<String, String> confirmationCrumb = new HashMap<>();
            confirmationCrumb.put(KEY_LABEL, LABEL_CONFIRMATION);
            confirmationCrumb.put(KEY_URL, null);
            breadcrumbs.add(confirmationCrumb);
            
            model.addAttribute(ATTR_BREADCRUMBS, breadcrumbs);
            
            return VIEW_RESERVATION_CONFIRMATION;
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage(), e);
            model.addAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
            model.addAttribute(ATTR_RESTAURANTS, restaurantService.getAllRestaurants());
            
            // Try to re-add the restaurant to the model
            restaurantService.getRestaurantById(restaurantId).ifPresent(restaurant -> 
                model.addAttribute(ATTR_RESTAURANT, restaurant));
            
            // Re-add menu if it was selected
            if (menuId != null) {
                menuService.getMenuById(menuId).ifPresent(menu -> 
                    model.addAttribute(ATTR_SELECTED_MENU, menu));
            }
            
            // Re-add the form data
            model.addAttribute(ATTR_RESERVATION_FORM, reservation);
            model.addAttribute(ATTR_RESERVATION_DATE, reservationDate);
            model.addAttribute(ATTR_RESERVATION_TIME, reservationTime);
            
            return VIEW_CREATE_RESERVATION;
        }
    }

    private void parseAndSetReservationDateTime(Reservation reservation, String dateString, String timeString) {
        try {
            // Parse the date (expected format yyyy-MM-dd)
            LocalDate date = LocalDate.parse(dateString);
            
            // Parse the time (handling different possible formats)
            LocalTime time = LocalTime.parse(timeString);
            
            // Combine into LocalDateTime
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            reservation.setReservationTime(dateTime);
            
            logger.info("Successfully set reservation time to: {}", dateTime);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date/time: {}", e.getMessage());
            throw new IllegalArgumentException("Please enter a valid date (YYYY-MM-DD) and time (HH:MM)");
        }
    }

    @GetMapping("s")
    public String viewReservations(Model model) {
        logger.info("Viewing customer reservations page");
        
        // Add breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> reservationsCrumb = new HashMap<>();
        reservationsCrumb.put(KEY_LABEL, LABEL_MY_RESERVATIONS);
        reservationsCrumb.put(KEY_URL, null);
        
        breadcrumbs.add(reservationsCrumb);
        
        model.addAttribute(ATTR_BREADCRUMBS, breadcrumbs);
        model.addAttribute(ATTR_PAGE_TITLE, "My Reservations - Moliceiro Meals");
        
        return VIEW_VIEW_RESERVATIONS;
    }

    @GetMapping("/{code}")
    public String viewReservation(@PathVariable String code, Model model) {
        try {
            var reservation = reservationService.getReservationByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            
            // Add breadcrumbs using Map.of for more concise code
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(Map.of(KEY_LABEL, LABEL_MY_RESERVATIONS, KEY_URL, "/customer/reservations"));
            breadcrumbs.add(Map.of(KEY_LABEL, "Reservation #" + code, KEY_URL, null));
            
            model.addAttribute(ATTR_RESERVATION, reservation);
            model.addAttribute(ATTR_BREADCRUMBS, breadcrumbs);
            model.addAttribute(ATTR_PAGE_TITLE, "Reservation Details - Moliceiro Meals");
            return VIEW_RESERVATION_DETAILS;
        } catch (Exception e) {
            model.addAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
            return VIEW_VIEW_RESERVATIONS;
        }
    }

    @PostMapping("/cancel/{code}")
    public String cancelReservation(@PathVariable String code, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Cancelling reservation with code: {}", code);
            reservationService.cancelReservation(code);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Reservation cancelled successfully");
            // Redirect to the view reservations page
            return "redirect:" + URL_CUSTOMER_RESERVATIONS;
        } catch (Exception e) {
            logger.error("Error cancelling reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
            return "redirect:/customer/reservation/" + code;
        }
    }

    @GetMapping("/find-by-email")
    public String findReservationsByEmail(@RequestParam String email, Model model) {
        logger.info("Finding reservations for email: {}", email);
        
        List<Reservation> reservations = reservationService.getReservationsByEmail(email);
        
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> myReservationsCrumb = new HashMap<>();
        myReservationsCrumb.put(KEY_LABEL, LABEL_MY_RESERVATIONS);
        myReservationsCrumb.put(KEY_URL, null);  
        
        breadcrumbs.add(myReservationsCrumb);
        
        model.addAttribute(ATTR_BREADCRUMBS, breadcrumbs);
        model.addAttribute(ATTR_RESERVATIONS, reservations);
        model.addAttribute(ATTR_PAGE_TITLE, "My Reservations - Moliceiro Meals");
        
        if (reservations.isEmpty()) {
            model.addAttribute(ATTR_INFO_MESSAGE, "No reservations found for email: " + email);
        }
        
        return VIEW_VIEW_RESERVATIONS;
    }

    @GetMapping("")
    public String checkReservation(@RequestParam(required = false) String code, Model model) {
        if (code != null && !code.trim().isEmpty()) {
            return "redirect:/customer/reservation/" + code;
        }
        
        return viewReservations(model);
    }
}