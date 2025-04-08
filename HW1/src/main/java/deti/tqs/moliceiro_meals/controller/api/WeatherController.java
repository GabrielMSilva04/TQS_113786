package deti.tqs.moliceiro_meals.controller.api;

import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.WeatherService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Please use 'yyyy-MM-dd'.");
        }
    }

    @GetMapping("/{location}/forecast")
    public List<WeatherData> getForecastForNextDays(@PathVariable String location, @RequestParam int days) {
        // Call the wrapper method with tracking for multi-day forecasts
        return weatherService.getForecastForNextDaysWithTracking(location, days);
    }
}