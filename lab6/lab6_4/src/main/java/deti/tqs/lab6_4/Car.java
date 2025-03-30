package deti.tqs.lab6_4;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;
    private String maker;
    private String model;
    private String category;  // e.g., "LUXURY", "COMPACT", "SUV"
    private boolean available = true;

    // Default constructor
    public Car() {
    }

    // Constructor with maker, model, and category
    public Car(String maker, String model, String category) {
        this.maker = maker;
        this.model = model;
        this.category = category;
    }

    // Getters and Setters
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Object methods overrides
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Car car = (Car) obj;
        return carId != null && carId.equals(car.carId);
    }

    @Override
    public int hashCode() {
        return carId != null ? carId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", maker='" + maker + '\'' +
                ", model='" + model + '\'' +
                ", category='" + category + '\'' +
                ", available=" + available +
                '}';
    }
}
