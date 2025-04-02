package deti.tqs.moliceiro_meals.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class WeatherData {
    
    private LocalDate date;
    private String location;
    private Double temperature;
    private String description;
    private Integer humidity;
    private Double windSpeed;

    // Constructors
    public WeatherData() {}

    public WeatherData(Instant timestamp, String location, Double temperature, String description, 
                      Integer humidity, Double windSpeed) {
        this.date = timestamp.atZone(ZoneId.systemDefault()).toLocalDate();
        this.location = location;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    // Getters and setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(Instant timestamp) {
        this.date = timestamp.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }
}