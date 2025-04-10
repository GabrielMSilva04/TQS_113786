package deti.tqs.moliceiro_meals.model;

import java.time.Instant;

public class WeatherData {
    private Instant timestamp;
    private String location;
    private Double temperature;
    private Double feelsLike;
    private Double maxTemp;
    private Double minTemp;
    private String description;
    private Integer humidity;
    private Double windSpeed;
    private String windDirection;
    private Double precipitation;
    private Integer chanceOfRain;
    private Double uvIndex;
    private String sunrise;
    private String sunset;
    private String icon;

    public WeatherData(Instant timestamp, String location, Double temperature, Double feelsLike,
                      Double maxTemp, Double minTemp, String description, Integer humidity, 
                      Double windSpeed, String windDirection, Double precipitation, Integer chanceOfRain, 
                      Double uvIndex, String sunrise, String sunset, String icon) {
        this.timestamp = timestamp;
        this.location = location;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.precipitation = precipitation;
        this.chanceOfRain = chanceOfRain;
        this.uvIndex = uvIndex;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.icon = icon;
    }

    // Update the existing constructor with fewer parameters
    public WeatherData(Instant timestamp, String location, Double temperature, Double feelsLike,
                      String description, Integer humidity, Double windSpeed, String windDirection,
                      Double precipitation, Integer chanceOfRain, Double uvIndex, String sunrise, String sunset, String icon) {
        this(timestamp, location, temperature, feelsLike, null, null, description, humidity, windSpeed,
             windDirection, precipitation, chanceOfRain, uvIndex, sunrise, sunset, icon);
    }

    // Fallback constructor for error cases
    public WeatherData(Instant timestamp, String location, Double temperature, String description, 
                      Integer humidity, Double windSpeed) {
        this(timestamp, location, temperature, null, null, null, description, humidity, windSpeed, 
             null, null, null, null, null, null, null);
    }

    // Getters and setters
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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

    public Double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(Double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Double minTemp) {
        this.minTemp = minTemp;
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

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Double precipitation) {
        this.precipitation = precipitation;
    }

    public Integer getChanceOfRain() {
        return chanceOfRain;
    }

    public void setChanceOfRain(Integer chanceOfRain) {
        this.chanceOfRain = chanceOfRain;
    }

    public Double getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(Double uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    // Helper methods for restaurant recommendations
    public boolean isGoodForOutdoorDining() {
        return temperature != null && temperature > 18 && temperature < 28 && 
               (chanceOfRain == null || chanceOfRain < 30) && 
               (windSpeed == null || windSpeed < 15);
    }

    public String getDiningRecommendation() {
        if (temperature == null) return "No recommendations available";

        if (isGoodForOutdoorDining()) {
            return "Great weather for outdoor dining!";
        } else if (temperature > 28) {
            return "It's hot today! Look for restaurants with good air conditioning or shaded outdoor areas.";
        } else if (temperature < 16) {
            return "It's chilly today. Enjoy indoor dining with warm, comforting meals.";
        } else if (chanceOfRain != null && chanceOfRain > 50) {
            return "High chance of rain. Best to choose restaurants with indoor seating.";
        } else {
            return "Good day to enjoy any restaurant!";
        }
    }

    public String getIconUrl() {
        if (icon != null) {
            return "https:" + icon;
        }
        return null;
    }

    // Add this method to generate a default wind direction based on weather conditions
    public String getEffectiveWindDirection() {
        if (windDirection != null) {
            return windDirection;
        }
        
        // If we have no wind direction but have wind speed, provide a generic direction
        if (windSpeed != null && windSpeed > 0) {
            return "Variable";
        }
        
        return "Calm";
    }

    // Update the getCardinalWindDirection method to use the effective direction
    public String getCardinalWindDirection() {
        String effectiveDir = getEffectiveWindDirection();
        
        if (effectiveDir == null || effectiveDir.equals("Calm") || effectiveDir.equals("Variable")) {
            return effectiveDir;
        }
        
        // Common abbreviations: N, NNE, NE, ENE, E, ESE, SE, SSE, S, SSW, SW, WSW, W, WNW, NW, NNW
        return switch (effectiveDir.toUpperCase()) {
            case "N" -> "North";
            case "NNE" -> "North-Northeast";
            case "NE" -> "Northeast";
            case "ENE" -> "East-Northeast";
            case "E" -> "East";
            case "ESE" -> "East-Southeast";
            case "SE" -> "Southeast";
            case "SSE" -> "South-Southeast";
            case "S" -> "South";
            case "SSW" -> "South-Southwest";
            case "SW" -> "Southwest";
            case "WSW" -> "West-Southwest";
            case "W" -> "West";
            case "WNW" -> "West-Northwest";
            case "NW" -> "Northwest";
            case "NNW" -> "North-Northwest";
            default -> windDirection;
        };
    }
}