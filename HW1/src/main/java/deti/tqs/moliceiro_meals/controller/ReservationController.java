package deti.tqs.moliceiro_meals.controller;

import deti.tqs.moliceiro_meals.model.Reservation;
import deti.tqs.moliceiro_meals.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation, @RequestParam Long restaurantId) {
        return reservationService.createReservation(reservation, restaurantId);
    }

    @GetMapping("/{id}")
    public Optional<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    // @DeleteMapping("/{id}")
    // public void cancelReservation(@PathVariable Long id) {
    //     reservationService.cancelReservation(id);
    // }

    // @PostMapping("/{id}/use")
    // public void markReservationAsUsed(@PathVariable Long id) {
    //     reservationService.markReservationAsUsed(id);
    // }
}