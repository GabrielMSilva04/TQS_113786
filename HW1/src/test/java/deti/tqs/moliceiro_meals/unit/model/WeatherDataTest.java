package deti.tqs.moliceiro_meals.unit.model;

import deti.tqs.moliceiro_meals.model.WeatherData;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDataTest {

    @Test
    void testConstructorAndGetters() {
        Instant timestamp = Instant.now();
        WeatherData weatherData = new WeatherData(
                timestamp, "Aveiro", 12.5, "Sunny", 80, 15.0
        );

        assertEquals(timestamp, weatherData.getTimestamp());
        assertEquals("Aveiro", weatherData.getLocation());
        assertEquals(12.5, weatherData.getTemperature());
        assertEquals("Sunny", weatherData.getDescription());
        assertEquals(80, weatherData.getHumidity());
        assertEquals(15.0, weatherData.getWindSpeed());
    }

    @Test
    void testSetters() {
        // Create with minimal constructor
        Instant timestamp = Instant.now();
        WeatherData weatherData = new WeatherData(
                timestamp, "TestLocation", 10.0, "Initial description", 50, 5.0
        );

        // Use setters to modify the data
        Instant newTimestamp = Instant.now().plusSeconds(3600);
        weatherData.setTimestamp(newTimestamp);
        weatherData.setLocation("Porto");
        weatherData.setTemperature(18.0);
        weatherData.setDescription("Cloudy");
        weatherData.setHumidity(70);
        weatherData.setWindSpeed(10.0);
        weatherData.setMaxTemp(22.5);
        weatherData.setMinTemp(15.0);
        weatherData.setWindDirection("SW");
        weatherData.setPrecipitation(2.5);
        weatherData.setChanceOfRain(40);

        // Test the updated values
        assertEquals(newTimestamp, weatherData.getTimestamp());
        assertEquals("Porto", weatherData.getLocation());
        assertEquals(18.0, weatherData.getTemperature());
        assertEquals("Cloudy", weatherData.getDescription());
        assertEquals(70, weatherData.getHumidity());
        assertEquals(10.0, weatherData.getWindSpeed());
        assertEquals(22.5, weatherData.getMaxTemp());
        assertEquals(15.0, weatherData.getMinTemp());
        assertEquals("SW", weatherData.getWindDirection());
        assertEquals(2.5, weatherData.getPrecipitation());
        assertEquals(40, weatherData.getChanceOfRain());
    }
    
    @Test
    void testIsGoodForOutdoorDining() {
        // Good conditions
        WeatherData goodWeather = new WeatherData(
                Instant.now(), "Aveiro", 22.0, "Sunny", 60, 10.0
        );
        goodWeather.setChanceOfRain(20);
        
        // Bad conditions (too hot)
        WeatherData tooHot = new WeatherData(
                Instant.now(), "Aveiro", 30.0, "Sunny", 40, 5.0
        );
        
        // Bad conditions (too windy)
        WeatherData tooWindy = new WeatherData(
                Instant.now(), "Aveiro", 22.0, "Windy", 50, 20.0
        );
        
        // Bad conditions (rain likely)
        WeatherData rainy = new WeatherData(
                Instant.now(), "Aveiro", 22.0, "Rainy", 80, 5.0
        );
        rainy.setChanceOfRain(60);
        
        assertTrue(goodWeather.isGoodForOutdoorDining());
        assertFalse(tooHot.isGoodForOutdoorDining());
        assertFalse(tooWindy.isGoodForOutdoorDining());
        assertFalse(rainy.isGoodForOutdoorDining());
    }

    @Test
    void testGetDiningRecommendation() {
        // Create different weather conditions and check recommendations
        WeatherData goodWeather = new WeatherData(
                Instant.now(), "Aveiro", 22.0, "Sunny", 60, 10.0
        );
        goodWeather.setChanceOfRain(20);
        
        WeatherData hotWeather = new WeatherData(
                Instant.now(), "Aveiro", 30.0, "Sunny", 40, 5.0
        );
        
        WeatherData coldWeather = new WeatherData(
                Instant.now(), "Aveiro", 15.0, "Cloudy", 70, 10.0
        );
        
        WeatherData rainyWeather = new WeatherData(
                Instant.now(), "Aveiro", 20.0, "Rain", 90, 5.0
        );
        rainyWeather.setChanceOfRain(75);
        
        assertTrue(goodWeather.getDiningRecommendation().contains("Great weather for outdoor dining"));
        assertTrue(hotWeather.getDiningRecommendation().contains("It's hot today"));
        assertTrue(coldWeather.getDiningRecommendation().contains("It's chilly today"));
        assertTrue(rainyWeather.getDiningRecommendation().contains("High chance of rain"));
    }
    
    @Test
    void testGetIconUrl() {
        WeatherData weather = new WeatherData(
                Instant.now(), "Aveiro", 22.0, "Sunny", 60, 10.0
        );
        
        // Initially null
        assertNull(weather.getIconUrl());
        
        // Set icon and test URL
        weather.setIcon("//cdn.weatherapi.com/weather/64x64/day/116.png");
        assertEquals("https://cdn.weatherapi.com/weather/64x64/day/116.png", weather.getIconUrl());
    }
    
    @Test
    void testWindDirectionMethods() {
        WeatherData weather = new WeatherData(
                Instant.now(), "Aveiro", 22.0, "Sunny", 60, 10.0
        );
        
        // Test with null wind direction
        assertEquals("Variable", weather.getEffectiveWindDirection());
        
        // Test with calm winds
        weather.setWindSpeed(0.0);
        assertEquals("Calm", weather.getEffectiveWindDirection());
        
        // Test with specific wind direction
        weather.setWindSpeed(15.0);
        weather.setWindDirection("SW");
        assertEquals("SW", weather.getEffectiveWindDirection());
        assertEquals("Southwest", weather.getCardinalWindDirection());
        
        // Test with another direction
        weather.setWindDirection("NNE");
        assertEquals("North-Northeast", weather.getCardinalWindDirection());
    }
}