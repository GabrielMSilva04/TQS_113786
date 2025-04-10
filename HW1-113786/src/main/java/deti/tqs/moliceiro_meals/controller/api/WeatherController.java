package deti.tqs.moliceiro_meals.controller.api;

import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.WeatherService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{location}")
    public WeatherData getWeatherForecast(@PathVariable String location, @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            // Call the wrapper method with tracking
            return weatherService.getWeatherForecastWithTracking(location, localDate);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Invalid date format. Please use 'yyyy-MM-dd'.");
        } catch (Exception e) {
            logger.error("Error getting weather forecast: {}", e.getMessage(), e);
            return new WeatherData(
                Instant.now(), location, null, 
                "Error retrieving forecast: " + e.getMessage(), null, null
            );
        }
    }

    @GetMapping("/{location}/forecast")
    public List<WeatherData> getForecastForNextDays(@PathVariable String location, 
                                                   @RequestParam(defaultValue = "5") int days) {
        try {
            // Validate input
            if (days < 1 || days > 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Days parameter must be between 1 and 10");
            }
            
            // Call the wrapper method with tracking for multi-day forecasts
            return weatherService.getForecastForNextDaysWithTracking(location, days);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting forecast for next days: {}", e.getMessage(), e);
            List<WeatherData> errorList = new ArrayList<>();
            errorList.add(new WeatherData(
                Instant.now(), location, null, 
                "Error retrieving forecast: " + e.getMessage(), null, null
            ));
            return errorList;
        }
    }
}