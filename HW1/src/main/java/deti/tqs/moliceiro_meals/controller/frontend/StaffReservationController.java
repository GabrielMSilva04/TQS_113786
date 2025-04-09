package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.ReservationService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff/reservations")
public class StaffReservationController {

    private static final Logger logger = LoggerFactory.getLogger(StaffReservationController.class);

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    public StaffReservationController(ReservationService reservationService, RestaurantService restaurantService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String getReservations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {
        
        logger.info("Getting reservations with filters - date: {}, status: {}, search: {}", date, status, search);
        
        List<Reservation> filteredReservations;
        
        // Apply filters
        if (date != null) {
            // Filter by date
            filteredReservations = reservationService.getReservationsByDate(date);
            model.addAttribute("dateFilter", date);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                ReservationStatus statusEnum = ReservationStatus.valueOf(status);
                filteredReservations = reservationService.getReservationsByStatus(statusEnum);
                model.addAttribute("statusFilter", status);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status: {}", status);
                filteredReservations = reservationService.getAllReservations();
            }
        } else if (search != null && !search.isEmpty()) {
            // Search by customer name, email or reservation code
            List<Reservation> byEmail = reservationService.getReservationsByEmail(search);
            List<Reservation> byName = reservationService.searchReservationsByCustomerName(search);
            Optional<Reservation> byCode = reservationService.getReservationByCode(search);
            
            // Combine the results
            filteredReservations = byEmail;
            filteredReservations.addAll(byName);
            byCode.ifPresent(filteredReservations::add);
            
            // Remove duplicates
            filteredReservations = filteredReservations.stream().distinct().collect(Collectors.toList());
            
            model.addAttribute("searchQuery", search);
        } else {
            // No filters, get all reservations
            filteredReservations = reservationService.getAllReservations();
        }
        
        // Group reservations by status
        List<Reservation> pendingReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .collect(Collectors.toList());
                
        List<Reservation> activeReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .collect(Collectors.toList());
                
        List<Reservation> checkedInReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
                .collect(Collectors.toList());
                
        List<Reservation> completedReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                .collect(Collectors.toList());
        
        // Add to model
        model.addAttribute("pendingReservations", pendingReservations);
        model.addAttribute("activeReservations", activeReservations);
        model.addAttribute("checkedInReservations", checkedInReservations);
        model.addAttribute("completedReservations", completedReservations);
        model.addAttribute("pageTitle", "Reservation Management - Staff Dashboard");
        
        return "pages/staff/reservations";
    }

    @GetMapping("/{id}")
    public String getReservationDetails(@PathVariable Long id, Model model) {
        logger.info("Getting details for reservation ID: {}", id);
        
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Reservation not found");
            return "redirect:/staff/reservations";
        }
        
        model.addAttribute("reservation", reservationOpt.get());
        model.addAttribute("pageTitle", "Reservation Details - Staff Dashboard");
        
        return "pages/staff/reservation-details";
    }

    @PostMapping("/{id}/confirm")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Confirming reservation ID: {}", id);
        
        try {
            reservationService.confirmReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation confirmed successfully");
        } catch (Exception e) {
            logger.error("Error confirming reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to confirm reservation: " + e.getMessage());
        }
        
        return "redirect:/staff/reservations";
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Cancelling reservation ID: {}", id);
        
        try {
            reservationService.staffCancelReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled successfully");
        } catch (Exception e) {
            logger.error("Error cancelling reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel reservation: " + e.getMessage());
        }
        
        return "redirect:/staff/reservations";
    }

    @PostMapping("/{id}/check-in")
    public String checkInReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Checking in reservation ID: {}", id);
        
        try {
            reservationService.checkInReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Guest checked in successfully");
        } catch (Exception e) {
            logger.error("Error checking in guest: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to check in guest: " + e.getMessage());
        }
        
        return "redirect:/staff/reservations";
    }

    @PostMapping("/{id}/complete")
    public String completeReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Completing reservation ID: {}", id);
        
        try {
            reservationService.completeReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation marked as completed");
        } catch (Exception e) {
            logger.error("Error completing reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to complete reservation: " + e.getMessage());
        }
        
        return "redirect:/staff/reservations";
    }
    
    // Add this method to handle allReservations that was previously in StaffController
    @GetMapping("/all")
    public String allReservations(Model model) {
        logger.info("Viewing all reservations");
        
        model.addAttribute("pendingReservations", reservationService.getReservationsByStatus(ReservationStatus.PENDING));
        model.addAttribute("activeReservations", reservationService.getReservationsByStatus(ReservationStatus.CONFIRMED));
        model.addAttribute("checkedInReservations", reservationService.getReservationsByStatus(ReservationStatus.CHECKED_IN));
        model.addAttribute("cancelledReservations", reservationService.getReservationsByStatus(ReservationStatus.CANCELLED));
        model.addAttribute("completedReservations", reservationService.getReservationsByStatus(ReservationStatus.COMPLETED));
        model.addAttribute("pageTitle", "All Reservations - Staff View");
        
        return "pages/staff/reservations";
    }
}