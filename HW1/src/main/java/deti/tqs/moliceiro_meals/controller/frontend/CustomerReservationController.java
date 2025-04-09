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
        model.addAttribute("reservation", reservation);
        
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> reservationCrumb = new HashMap<>();
        reservationCrumb.put("label", "Make Reservation");
        reservationCrumb.put("url", null);
        
        breadcrumbs.add(reservationCrumb);
        
        if (restaurantId != null) {
            logger.info("Fetching restaurant with ID: {}", restaurantId);
            var restaurantOpt = restaurantService.getRestaurantById(restaurantId);
            if (restaurantOpt.isPresent()) {
                Restaurant restaurant = restaurantOpt.get();
                model.addAttribute("restaurant", restaurant);
                
                Map<String, String> restaurantCrumb = new HashMap<>();
                restaurantCrumb.put("label", restaurant.getName());
                restaurantCrumb.put("url", "/customer/restaurant/" + restaurantId);
                breadcrumbs.add(1, restaurantCrumb);
                
                // If a menu was selected, add it to the model
                if (menuId != null) {
                    logger.info("Fetching menu with ID: {}", menuId);
                    Optional<Menu> selectedMenuOpt = menuService.getMenuById(menuId);
                    if (selectedMenuOpt.isPresent()) {
                        Menu selectedMenu = selectedMenuOpt.get();
                        model.addAttribute("selectedMenu", selectedMenu);
                        
                        Map<String, String> menuCrumb = new HashMap<>();
                        menuCrumb.put("label", "Menu: " + selectedMenu.getName());
                        menuCrumb.put("url", null);
                        breadcrumbs.add(2, menuCrumb);
                    } else {
                        logger.warn("Menu with ID {} not found", menuId);
                    }
                }
            } else {
                logger.warn("Restaurant with ID {} not found", restaurantId);
                model.addAttribute("errorMessage", "Restaurant not found");
            }
        }
        
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("pageTitle", "Make a Reservation - Moliceiro Meals");
        
        logger.info("Rendering create-reservation view");
        return "pages/customer/create-reservation";
    }

    @PostMapping("/create")
    public String submitReservation(
            @ModelAttribute("reservationForm") Reservation reservation,
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
                
                try {
                    // Parse the date (expected format yyyy-MM-dd)
                    LocalDate date = LocalDate.parse(reservationDate);
                    
                    // Parse the time (handling different possible formats)
                    LocalTime time = LocalTime.parse(reservationTime);
                    
                    // Combine into LocalDateTime
                    LocalDateTime dateTime = LocalDateTime.of(date, time);
                    reservation.setReservationTime(dateTime);
                    
                    logger.info("Successfully set reservation time to: {}", dateTime);
                } catch (DateTimeParseException e) {
                    logger.error("Error parsing date/time: {}", e.getMessage());
                    throw new IllegalArgumentException("Please enter a valid date (YYYY-MM-DD) and time (HH:MM)");
                }
            } else {
                throw new IllegalArgumentException("Reservation date and time are required");
            }
            
            // Get restaurant
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
            model.addAttribute("reservation", createdReservation);
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("pageTitle", "Reservation Confirmed - Moliceiro Meals");
            
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            
            Map<String, String> reservationsCrumb = new HashMap<>();
            reservationsCrumb.put("label", "Reservations");
            reservationsCrumb.put("url", "/customer/reservation/s");
            breadcrumbs.add(reservationsCrumb);
            
            Map<String, String> confirmationCrumb = new HashMap<>();
            confirmationCrumb.put("label", "Confirmation");
            confirmationCrumb.put("url", null);
            breadcrumbs.add(confirmationCrumb);
            
            model.addAttribute("breadcrumbs", breadcrumbs);
            
            return "pages/customer/reservation-confirmation";
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            
            // Try to re-add the restaurant to the model
            restaurantService.getRestaurantById(restaurantId).ifPresent(restaurant -> 
                model.addAttribute("restaurant", restaurant));
            
            // Re-add menu if it was selected
            if (menuId != null) {
                menuService.getMenuById(menuId).ifPresent(menu -> 
                    model.addAttribute("selectedMenu", menu));
            }
            
            // Re-add the form data
            model.addAttribute("reservationForm", reservation);
            model.addAttribute("reservationDate", reservationDate);
            model.addAttribute("reservationTime", reservationTime);
            
            return "pages/customer/create-reservation";
        }
    }

    @GetMapping("s")
    public String viewReservations(Model model) {
        logger.info("Viewing customer reservations page");
        
        // Add breadcrumbs
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> reservationsCrumb = new HashMap<>();
        reservationsCrumb.put("label", "My Reservations");
        reservationsCrumb.put("url", null);
        
        breadcrumbs.add(reservationsCrumb);
        
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("pageTitle", "My Reservations - Moliceiro Meals");
        
        return "pages/customer/view-reservations";
    }

    @GetMapping("/{code}")
    public String viewReservation(@PathVariable String code, Model model) {
        try {
            logger.info("Viewing details for reservation with code: {}", code);
            
            // Normalize the code
            String normalizedCode = code.trim().toUpperCase();
            
            var reservationOpt = reservationService.getReservationByCode(normalizedCode);
            if (reservationOpt.isEmpty()) {
                throw new IllegalArgumentException("Reservation not found with code: " + normalizedCode);
            }
            
            Reservation reservation = reservationOpt.get();
            logger.info("Found reservation details - ID: {}, Customer: {}, Restaurant: {}", 
                       reservation.getId(), 
                       reservation.getCustomerName(),
                       reservation.getRestaurant() != null ? reservation.getRestaurant().getName() : "Unknown");
            
            // Add breadcrumbs with proper HashMap creation to avoid NullPointerException
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            
            Map<String, String> homeMap = new HashMap<>();
            homeMap.put("label", "Home");
            homeMap.put("url", "/customer");
            breadcrumbs.add(homeMap);
            
            Map<String, String> reservationsMap = new HashMap<>();
            reservationsMap.put("label", "My Reservations");
            reservationsMap.put("url", "/customer/reservation/s");
            breadcrumbs.add(reservationsMap);
            
            Map<String, String> detailsMap = new HashMap<>();
            detailsMap.put("label", "Reservation #" + code);
            detailsMap.put("url", null);
            breadcrumbs.add(detailsMap);
            
            model.addAttribute("reservation", reservation);
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", "Reservation Details - Moliceiro Meals");
            
            return "pages/customer/reservation-details";
        } catch (Exception e) {
            logger.error("Error viewing reservation details: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            
            // Add breadcrumbs
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            
            Map<String, String> reservationsCrumb = new HashMap<>();
            reservationsCrumb.put("label", "My Reservations");
            reservationsCrumb.put("url", "/customer/reservation/s");
            breadcrumbs.add(reservationsCrumb);
            
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", "Reservation Not Found - Moliceiro Meals");
            
            return "pages/customer/view-reservations";
        }
    }

    @PostMapping("/cancel/{code}")
    public String cancelReservation(@PathVariable String code, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Cancelling reservation with code: {}", code);
            reservationService.cancelReservation(code);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled successfully");
            // Redirect to the view reservations page
            return "redirect:/customer/reservation/s";
        } catch (Exception e) {
            logger.error("Error cancelling reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customer/reservation/" + code;
        }
    }

    @GetMapping("/find-by-email")
    public String findReservationsByEmail(@RequestParam String email, Model model) {
        logger.info("Finding reservations for email: {}", email);
        
        List<Reservation> reservations = reservationService.getReservationsByEmail(email);
        
        List<Map<String, String>> breadcrumbs = new ArrayList<>();
        
        Map<String, String> myReservationsCrumb = new HashMap<>();
        myReservationsCrumb.put("label", "My Reservations");
        myReservationsCrumb.put("url", null);
        
        breadcrumbs.add(myReservationsCrumb);
        
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("reservations", reservations);
        model.addAttribute("pageTitle", "My Reservations - Moliceiro Meals");
        
        if (reservations.isEmpty()) {
            model.addAttribute("infoMessage", "No reservations found for email: " + email);
        }
        
        return "pages/customer/view-reservations";
    }

    @GetMapping("")
    public String checkReservation(@RequestParam(required = false) String code, Model model) {
        logger.info("Looking up reservation with code: '{}'", code);
        
        if (code == null || code.trim().isEmpty()) {
            // Return a form to enter the code
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            
            Map<String, String> checkCrumb = new HashMap<>();
            checkCrumb.put("label", "Check Reservation");
            checkCrumb.put("url", null);
            
            breadcrumbs.add(checkCrumb);
            
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", "Check Reservation - Moliceiro Meals");
            
            return "pages/customer/check-reservation";
        }
        
        try {
            // Normalize the code input
            String normalizedCode = code.trim().toUpperCase();
            logger.info("Looking up reservation with normalized code: '{}'", normalizedCode);
            
            // Try to find the reservation
            Optional<Reservation> reservationOpt = reservationService.getReservationByCode(normalizedCode);
            
            if (reservationOpt.isPresent()) {
                // Found the reservation, redirect to detailed view
                Reservation reservation = reservationOpt.get();
                logger.info("Found reservation: ID={}, Token={}, Customer={}", 
                           reservation.getId(), reservation.getToken(), reservation.getCustomerName());
                return "redirect:/customer/reservation/" + reservation.getToken();
            } else {
                // Reservation not found
                logger.warn("No reservation found with code: '{}'", normalizedCode);
                model.addAttribute("errorMessage", "No reservation found with code: " + normalizedCode);
                
                List<Map<String, String>> breadcrumbs = new ArrayList<>();
                
                Map<String, String> checkCrumb = new HashMap<>();
                checkCrumb.put("label", "Check Reservation");
                checkCrumb.put("url", null);
                
                breadcrumbs.add(checkCrumb);
                
                model.addAttribute("breadcrumbs", breadcrumbs);
                model.addAttribute("pageTitle", "Check Reservation - Moliceiro Meals");
                
                return "pages/customer/check-reservation";
            }
        } catch (Exception e) {
            logger.error("Error looking up reservation: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "An error occurred while looking up your reservation. Please try again.");
            
            List<Map<String, String>> breadcrumbs = new ArrayList<>();
            
            Map<String, String> checkCrumb = new HashMap<>();
            checkCrumb.put("label", "Check Reservation");
            checkCrumb.put("url", null);
            
            breadcrumbs.add(checkCrumb);
            
            model.addAttribute("breadcrumbs", breadcrumbs);
            model.addAttribute("pageTitle", "Check Reservation - Moliceiro Meals");
            
            return "pages/customer/check-reservation";
        }
    }
}