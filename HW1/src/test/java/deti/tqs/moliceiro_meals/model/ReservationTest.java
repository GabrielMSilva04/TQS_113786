package deti.tqs.moliceiro_meals.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void testConstructorAndGetters() {
        Restaurant restaurant = new Restaurant();
        LocalDateTime reservationTime = LocalDateTime.now();
        Reservation reservation = new Reservation(
                "John Doe", "john@example.com", "123456789", 4, reservationTime, "No special requests", restaurant
        );

        assertEquals("John Doe", reservation.getCustomerName());
        assertEquals("john@example.com", reservation.getCustomerEmail());
        assertEquals("123456789", reservation.getCustomerPhone());
        assertEquals(4, reservation.getPartySize());
        assertEquals(reservationTime, reservation.getReservationTime());
        assertEquals("No special requests", reservation.getSpecialRequests());
        assertEquals(restaurant, reservation.getRestaurant());
    }

    @Test
    void testSetters() {
        Reservation reservation = new Reservation();
        Restaurant restaurant = new Restaurant();
        LocalDateTime reservationTime = LocalDateTime.now();

        reservation.setCustomerName("Jane Doe");
        reservation.setCustomerEmail("jane@example.com");
        reservation.setCustomerPhone("987654321");
        reservation.setPartySize(2);
        reservation.setReservationTime(reservationTime);
        reservation.setSpecialRequests("Window seat");
        reservation.setRestaurant(restaurant);

        assertEquals("Jane Doe", reservation.getCustomerName());
        assertEquals("jane@example.com", reservation.getCustomerEmail());
        assertEquals("987654321", reservation.getCustomerPhone());
        assertEquals(2, reservation.getPartySize());
        assertEquals(reservationTime, reservation.getReservationTime());
        assertEquals("Window seat", reservation.getSpecialRequests());
        assertEquals(restaurant, reservation.getRestaurant());
    }

    @Test
    void testReservationTokenGeneration() {
        Reservation reservation = new Reservation();
        assertNotNull(reservation.getToken());
        assertEquals(8, reservation.getToken().length());
    }
}