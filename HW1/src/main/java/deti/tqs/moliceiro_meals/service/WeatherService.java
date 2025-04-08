package deti.tqs.moliceiro_meals.service;

import com.fasterxml.jackson.databind.JsonNode;
import deti.tqs.moliceiro_meals.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private static final String FORECAST = "forecast";

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WebClient webClient;
    private final CacheManager cacheManager;

    @Value("${weather.api.key}")
    private String apiKey;

    private int cacheHits = 0;
    private int cacheMisses = 0;
    private int totalRequests = 0;

    public WeatherService(WebClient.Builder webClientBuilder, CacheManager cacheManager) {
        this.webClient = webClientBuilder.baseUrl("https://api.weatherapi.com/v1").build();
        this.cacheManager = cacheManager;
    }

    public void incrementCacheHits() {
        cacheHits++;
    }

    public void incrementCacheMisses() {
        cacheMisses++;
    }

    public void incrementTotalRequests() {
        totalRequests++;
    }

    public Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("hits", cacheHits);
        stats.put("misses", cacheMisses);
        stats.put("totalRequests", totalRequests);
        return stats;
    }

    public WeatherData getWeatherForecastWithTracking(String location, LocalDate date) {
        incrementTotalRequests();

        // Generate the cache key
        String key = location + "-" + date;

        // Manually check the cache
        Cache cache = cacheManager.getCache("weatherForecast");
        if (cache != null) {
            Cache.ValueWrapper cachedValue = cache.get(key);
            if (cachedValue != null) {
                incrementCacheHits(); // Count a hit
                logger.info("Cache hit for key: {}", key);
                return (WeatherData) cachedValue.get();
            }
        }

        incrementCacheMisses();
        logger.info("Cache miss for key: {}", key);
        return getWeatherForecast(location, date);
    }

    @Cacheable(value = "weatherForecast", key = "#location + '-' + #date")
    public WeatherData getWeatherForecast(String location, LocalDate date) {
        return fetchWeatherFromApi(location, date);
    }

    @Cacheable(value = "weatherForecast", key = "#location + '-next-' + #days")
    public List<WeatherData> getForecastForNextDays(String location, int days) {
        return fetchWeatherForMultipleDays(location, days);
    }

    public List<WeatherData> getForecastForNextDaysWithTracking(String location, int days) {
        incrementTotalRequests();
        
        // Generate the cache key
        String key = location + "-next-" + days;
        
        logger.info("Checking cache for key: {}, current stats: hits={}, misses={}, total={}", 
                    key, cacheHits, cacheMisses, totalRequests);
        
        // Manually check the cache
        Cache cache = cacheManager.getCache("weatherForecast");
        if (cache != null) {
            Cache.ValueWrapper cachedValue = cache.get(key);
            if (cachedValue != null && cachedValue.get() instanceof List<?>) {
                incrementCacheHits(); // Count a hit
                logger.info("Cache hit for key: {}, hits now: {}", key, cacheHits);
                return (List<WeatherData>) cachedValue.get();
            }
        }
        
        incrementCacheMisses();
        logger.info("Cache miss for key: {}, misses now: {}", key, cacheMisses);
        
        // Get the data directly from the API without using the @Cacheable method
        List<WeatherData> forecast = fetchWeatherForMultipleDays(location, days);
        
        // Manually put in cache
        if (cache != null) {
            cache.put(key, forecast);
        }
        
        return forecast;
    }

    private WeatherData fetchWeatherFromApi(String location, LocalDate date) {
        logger.info("Fetching weather data for {} on {}", location, date);

        try {
            String url = String.format("/forecast.json?key=%s&q=%s&days=1", apiKey, location);

            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (!response.has("forecast") || response.get("forecast").get("forecastday").isEmpty()) {
                logger.error("Empty forecast response");
                return new WeatherData(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), location, null, "No data available", null, null);
            }

            String loc = response.get("location").get("name").asText();
            JsonNode forecastDay = response.get("forecast").get("forecastday").get(0);
            Instant timestamp = Instant.parse(forecastDay.get("date").asText() + "T00:00:00Z");
            Double temp = forecastDay.get("day").get("avgtemp_c").asDouble();
            String desc = forecastDay.get("day").get("condition").get("text").asText();
            Integer hum = forecastDay.get("day").get("avghumidity").asInt();
            Double wind = forecastDay.get("day").get("maxwind_kph").asDouble();

            return new WeatherData(timestamp, loc, temp, desc, hum, wind);
        } catch (Exception e) {
            logger.error("Error fetching weather data: {}", e.getMessage(), e);
            return new WeatherData(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), location, null, "Error fetching data", null, null);
        }
    }

    private List<WeatherData> fetchWeatherForMultipleDays(String location, int days) {
        logger.info("Fetching weather forecast for {} for the next {} days", location, days);
        try {
            String url = String.format("/forecast.json?key=%s&q=%s&days=%d", apiKey, location, days);

            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (!response.has(FORECAST) || response.get(FORECAST).get("forecastday").isEmpty()) {
                logger.error("Empty forecast response");
                return new ArrayList<>();
            }

            List<WeatherData> forecast = new ArrayList<>();
            JsonNode forecastDays = response.get(FORECAST).get("forecastday");
            for (JsonNode forecastDay : forecastDays) {
                LocalDate date = LocalDate.parse(forecastDay.get("date").asText());
                Double temp = forecastDay.get("day").get("avgtemp_c").asDouble();
                String desc = forecastDay.get("day").get("condition").get("text").asText();
                Integer hum = forecastDay.get("day").get("avghumidity").asInt();
                Double wind = forecastDay.get("day").get("maxwind_kph").asDouble();

                WeatherData weatherData = new WeatherData(
                        date.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        location,
                        temp,
                        desc,
                        hum,
                        wind
                );
                forecast.add(weatherData);
            }

            return forecast;
        } catch (Exception e) {
            logger.error("Error fetching weather forecast: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @CacheEvict(value = "weatherForecast", allEntries = true)
    @Scheduled(fixedRateString = "${weather.cache.ttl:3600000}")
    public void evictCache() {
        logger.info("Evicting weather forecast cache");
    }
}