package deti.tqs.moliceiro_meals.service;

import com.fasterxml.jackson.databind.JsonNode;
import deti.tqs.moliceiro_meals.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final WebClient webClient;

    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.weatherapi.com/v1").build();
    }

    public String getApiKey() {
        return apiKey;
    }

    @Cacheable(value = "weatherForecast", key = "#location + '-' + #date")
    public WeatherData getWeatherForecast(String location, LocalDate date) {
        totalRequests.incrementAndGet();
        logger.info("Fetching weather data for {} on {}", location, date);

        try {
            String url = String.format("/forecast.json?key=%s&q=%s&days=1", apiKey, location);
            cacheMisses.incrementAndGet();

            // Fetch and parse the API response
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class) // Use JsonNode for flexible parsing
                    .map(response -> {
                        String loc = response.get("location").get("name").asText();
                        JsonNode forecastDay = response.get("forecast").get("forecastday").get(0);
                        Instant timestamp = Instant.parse(forecastDay.get("date").asText() + "T00:00:00Z");
                        Double temp = forecastDay.get("day").get("avgtemp_c").asDouble();
                        String desc = forecastDay.get("day").get("condition").get("text").asText();
                        Integer hum = forecastDay.get("day").get("avghumidity").asInt();
                        Double wind = forecastDay.get("day").get("maxwind_kph").asDouble();

                        return new WeatherData(timestamp, loc, temp, desc, hum, wind);
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Error fetching weather data: {}", e.getMessage(), e);
            return new WeatherData(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), location, null, "Error fetching data", null, null);
        }
    }

    public List<WeatherData> getForecastForNextDays(String location, int days) {
        List<WeatherData> forecast = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < days; i++) {
            forecast.add(getWeatherForecast(location, today.plusDays(i)));
        }

        return forecast;
    }

    @CacheEvict(value = "weatherForecast", allEntries = true)
    @Scheduled(fixedRateString = "${weather.cache.ttl:3600000}")
    public void evictCache() {
        logger.info("Evicting weather forecast cache");
    }

    public void recordCacheHit() {
        cacheHits.incrementAndGet();
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public int getCacheHits() {
        return cacheHits.get();
    }

    public int getCacheMisses() {
        return cacheMisses.get();
    }
}