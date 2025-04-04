package deti.tqs.moliceiro_meals.controller;

import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{location}")
    public WeatherData getWeatherForecast(@PathVariable String location, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return weatherService.getWeatherForecast(location, localDate);
    }

    @GetMapping("/{location}/forecast")
    public List<WeatherData> getForecastForNextDays(@PathVariable String location, @RequestParam int days) {
        return weatherService.getForecastForNextDays(location, days);
    }
}