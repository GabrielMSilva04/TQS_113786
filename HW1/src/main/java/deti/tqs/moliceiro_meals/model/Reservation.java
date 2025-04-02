package deti.tqs.moliceiro_meals.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private int partySize;
    private LocalDateTime reservationTime;
    private String specialRequests;
    
    @Column(unique = true)
    private String token;
    
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    // Constructors
    public Reservation() {
        this.token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = ReservationStatus.PENDING;
    }
    
    public Reservation(String customerName, String customerEmail, String customerPhone, 
                      int partySize, LocalDateTime reservationTime, String specialRequests, 
                      Restaurant restaurant) {
        this();
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.partySize = partySize;
        this.reservationTime = reservationTime;
        this.specialRequests = specialRequests;
        this.restaurant = restaurant;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}