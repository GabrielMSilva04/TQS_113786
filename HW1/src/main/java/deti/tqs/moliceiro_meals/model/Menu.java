package deti.tqs.moliceiro_meals.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private LocalDate date;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MenuItem> items = new ArrayList<>();

    // Constructors
    public Menu() {}
    
    public Menu(String name, String description, LocalDate date, Restaurant restaurant) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.restaurant = restaurant;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }
}