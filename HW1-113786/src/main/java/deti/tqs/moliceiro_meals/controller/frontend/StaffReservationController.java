package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.model.ReservationStatus;
import deti.tqs.moliceiro_meals.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Controller
@RequestMapping("/staff/reservations")
public class StaffReservationController {

    private static final Logger logger = LoggerFactory.getLogger(StaffReservationController.class);
    
    // View constants
    private static final String VIEW_RESERVATIONS = "pages/staff/reservations";
    private static final String VIEW_RESERVATION_DETAILS = "pages/staff/reservation-details";
    
    // Redirect constants
    private static final String REDIRECT_RESERVATIONS = "redirect:/staff/reservations";
    
    // Model attribute constants
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_PAGE_TITLE = "pageTitle";
    private static final String ATTR_RESERVATION = "reservation";
    private static final String ATTR_DATE_FILTER = "dateFilter";
    private static final String ATTR_STATUS_FILTER = "statusFilter";
    private static final String ATTR_SEARCH_QUERY = "searchQuery";
    private static final String ATTR_PENDING_RESERVATIONS = "pendingReservations";
    private static final String ATTR_ACTIVE_RESERVATIONS = "activeReservations";
    private static final String ATTR_CHECKED_IN_RESERVATIONS = "checkedInReservations";
    private static final String ATTR_COMPLETED_RESERVATIONS = "completedReservations";
    private static final String ATTR_CANCELLED_RESERVATIONS = "cancelledReservations";
    
    // Error message constants
    private static final String ERROR_RESERVATION_NOT_FOUND = "Reservation not found";
    private static final String ERROR_CONFIRM_RESERVATION = "Failed to confirm reservation: ";
    private static final String ERROR_CANCEL_RESERVATION = "Failed to cancel reservation: ";
    private static final String ERROR_CHECK_IN = "Failed to check in guest: ";
    private static final String ERROR_COMPLETE_RESERVATION = "Failed to complete reservation: ";
    
    // Success message constants
    private static final String SUCCESS_CONFIRM_RESERVATION = "Reservation confirmed successfully";
    private static final String SUCCESS_CANCEL_RESERVATION = "Reservation cancelled successfully";
    private static final String SUCCESS_CHECK_IN = "Guest checked in successfully";
    private static final String SUCCESS_COMPLETE_RESERVATION = "Reservation marked as completed";

    private final ReservationService reservationService;

    public StaffReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
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
            model.addAttribute(ATTR_DATE_FILTER, date);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                ReservationStatus statusEnum = ReservationStatus.valueOf(status);
                filteredReservations = reservationService.getReservationsByStatus(statusEnum);
                model.addAttribute(ATTR_STATUS_FILTER, status);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status: {}", status);
                filteredReservations = reservationService.getAllReservations();
            }
        } else if (search != null && !search.isEmpty()) {
            // Search by customer name, email or reservation code
            List<Reservation> byEmail = reservationService.getReservationsByEmail(search);
            List<Reservation> byName = reservationService.searchReservationsByCustomerName(search);
            Optional<Reservation> byCode = reservationService.getReservationByCode(search);
            
            // Combine the results and remove duplicates in one step
            filteredReservations = new ArrayList<>();
            filteredReservations.addAll(byEmail);
            filteredReservations.addAll(byName);
            byCode.ifPresent(filteredReservations::add);
            
            // Use toList() to create an unmodifiable list with distinct elements
            filteredReservations = filteredReservations.stream().distinct().toList();
            
            model.addAttribute(ATTR_SEARCH_QUERY, search);
        } else {
            filteredReservations = reservationService.getAllReservations();
        }
        
        List<Reservation> pendingReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .toList();
                
        List<Reservation> activeReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();
                
        List<Reservation> checkedInReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
                .toList();
                
        List<Reservation> completedReservations = filteredReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                .toList();
        
        model.addAttribute(ATTR_PENDING_RESERVATIONS, pendingReservations);
        model.addAttribute(ATTR_ACTIVE_RESERVATIONS, activeReservations);
        model.addAttribute(ATTR_CHECKED_IN_RESERVATIONS, checkedInReservations);
        model.addAttribute(ATTR_COMPLETED_RESERVATIONS, completedReservations);
        model.addAttribute(ATTR_PAGE_TITLE, "Reservation Management - Staff Dashboard");
        
        return VIEW_RESERVATIONS;
    }

    @GetMapping("/{id}")
    public String getReservationDetails(@PathVariable Long id, Model model) {
        logger.info("Getting details for reservation ID: {}", id);
        
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isEmpty()) {
            model.addAttribute(ATTR_ERROR_MESSAGE, ERROR_RESERVATION_NOT_FOUND);
            return REDIRECT_RESERVATIONS;
        }
        
        model.addAttribute(ATTR_RESERVATION, reservationOpt.get());
        model.addAttribute(ATTR_PAGE_TITLE, "Reservation Details - Staff Dashboard");
        
        return VIEW_RESERVATION_DETAILS;
    }

    @PostMapping("/{id}/confirm")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Confirming reservation ID: {}", id);
        
        try {
            reservationService.confirmReservation(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_CONFIRM_RESERVATION);
        } catch (Exception e) {
            logger.error("Error confirming reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_CONFIRM_RESERVATION + e.getMessage());
        }
        
        return REDIRECT_RESERVATIONS;
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Cancelling reservation ID: {}", id);
        
        try {
            reservationService.staffCancelReservation(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_CANCEL_RESERVATION);
        } catch (Exception e) {
            logger.error("Error cancelling reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_CANCEL_RESERVATION + e.getMessage());
        }
        
        return REDIRECT_RESERVATIONS;
    }

    @PostMapping("/{id}/check-in")
    public String checkInReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Checking in reservation ID: {}", id);
        
        try {
            reservationService.checkInReservation(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_CHECK_IN);
        } catch (Exception e) {
            logger.error("Error checking in guest: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_CHECK_IN + e.getMessage());
        }
        
        return REDIRECT_RESERVATIONS;
    }

    @PostMapping("/{id}/complete")
    public String completeReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Completing reservation ID: {}", id);
        
        try {
            reservationService.completeReservation(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_COMPLETE_RESERVATION);
        } catch (Exception e) {
            logger.error("Error completing reservation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_COMPLETE_RESERVATION + e.getMessage());
        }
        
        return REDIRECT_RESERVATIONS;
    }
    
    @GetMapping("/all")
    public String allReservations(Model model) {
        logger.info("Viewing all reservations");
        
        model.addAttribute(ATTR_PENDING_RESERVATIONS, reservationService.getReservationsByStatus(ReservationStatus.PENDING));
        model.addAttribute(ATTR_ACTIVE_RESERVATIONS, reservationService.getReservationsByStatus(ReservationStatus.CONFIRMED));
        model.addAttribute(ATTR_CHECKED_IN_RESERVATIONS, reservationService.getReservationsByStatus(ReservationStatus.CHECKED_IN));
        model.addAttribute(ATTR_CANCELLED_RESERVATIONS, reservationService.getReservationsByStatus(ReservationStatus.CANCELLED));
        model.addAttribute(ATTR_COMPLETED_RESERVATIONS, reservationService.getReservationsByStatus(ReservationStatus.COMPLETED));
        model.addAttribute(ATTR_PAGE_TITLE, "All Reservations - Staff View");
        
        return VIEW_RESERVATIONS;
    }
}