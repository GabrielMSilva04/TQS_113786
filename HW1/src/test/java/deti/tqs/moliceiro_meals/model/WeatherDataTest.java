package deti.tqs.moliceiro_meals.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDataTest {

    @Test
    void testConstructorAndGetters() {
        Instant timestamp = Instant.now();
        WeatherData weatherData = new WeatherData(
                timestamp, "Aveiro", 12.5, "Sunny", 80, 15.0
        );

        assertEquals(LocalDate.now(), weatherData.getDate());
        assertEquals("Aveiro", weatherData.getLocation());
        assertEquals(12.5, weatherData.getTemperature());
        assertEquals("Sunny", weatherData.getDescription());
        assertEquals(80, weatherData.getHumidity());
        assertEquals(15.0, weatherData.getWindSpeed());
    }

    @Test
    void testSetters() {
        WeatherData weatherData = new WeatherData();

        weatherData.setDate(Instant.now());
        weatherData.setLocation("Porto");
        weatherData.setTemperature(18.0);
        weatherData.setDescription("Cloudy");
        weatherData.setHumidity(70);
        weatherData.setWindSpeed(10.0);

        assertEquals("Porto", weatherData.getLocation());
        assertEquals(18.0, weatherData.getTemperature());
        assertEquals("Cloudy", weatherData.getDescription());
        assertEquals(70, weatherData.getHumidity());
        assertEquals(10.0, weatherData.getWindSpeed());
    }
}