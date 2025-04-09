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
            // Add &aqi=yes to get air quality data
            String url = String.format("/forecast.json?key=%s&q=%s&days=1&aqi=yes", apiKey, location);

            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null || !response.has("forecast") || 
                !response.get("forecast").has("forecastday") || 
                response.get("forecast").get("forecastday").isEmpty()) {
                logger.error("Empty forecast response");
                return new WeatherData(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                                      location, null, null, null, null, 
                                      "No data available", null, null, null, null, null, null, null, null, null);
            }

            String loc = safeGetString(response.get("location"), "name");
            if (loc == null) loc = location;
            
            JsonNode forecastDay = response.get("forecast").get("forecastday").get(0);
            JsonNode day = forecastDay.get("day");
            
            Instant timestamp = Instant.parse(safeGetString(forecastDay, "date") + "T00:00:00Z");
            Double temp = safeGetDouble(day, "avgtemp_c");
            Double feelsLike = safeGetDouble(day, "avgfeelslike_c");
            Double maxTemp = safeGetDouble(day, "maxtemp_c");
            Double minTemp = safeGetDouble(day, "mintemp_c");
            String desc = safeGetNestedString(day, "condition", "text");
            Integer hum = safeGetInt(day, "avghumidity");
            Double wind = safeGetDouble(day, "maxwind_kph");
            
            // Extract wind direction - check multiple possible locations in the API response
            String windDir = null;
            // Try getting wind_dir from the day object
            windDir = safeGetString(day, "wind_dir");
            
            // If not available, try getting it from the current condition
            if (windDir == null && response.has("current") && response.get("current").has("wind_dir")) {
                windDir = safeGetString(response.get("current"), "wind_dir");
            }
            
            // If still not available, try getting from the first hour of the forecast
            if (windDir == null && forecastDay.has("hour") && !forecastDay.get("hour").isEmpty()) {
                JsonNode firstHour = forecastDay.get("hour").get(0);
                windDir = safeGetString(firstHour, "wind_dir");
            }
            
            Double precip = safeGetDouble(day, "totalprecip_mm");
            Integer rainChance = safeGetInt(day, "daily_chance_of_rain");
            Double uv = safeGetDouble(day, "uv");
            
            // Get sunrise/sunset
            JsonNode astroNode = forecastDay.has("astro") ? forecastDay.get("astro") : null;
            String sunrise = astroNode != null ? safeGetString(astroNode, "sunrise") : null;
            String sunset = astroNode != null ? safeGetString(astroNode, "sunset") : null;
            
            // Get weather icon
            String icon = safeGetNestedString(day, "condition", "icon");

            return new WeatherData(
                timestamp, loc, temp, feelsLike, 
                maxTemp, minTemp,
                desc != null ? desc : "No description available", 
                hum, wind, windDir,  // Pass the extracted wind direction
                precip, rainChance, uv, 
                sunrise, sunset, icon
            );
        } catch (Exception e) {
            logger.error("Error fetching weather data: {}", e.getMessage(), e);
            return new WeatherData(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                                  location, null, null, null, null, 
                                  "Error fetching data", null, null, null, null, null, null, null, null, null);
        }
    }

    private List<WeatherData> fetchWeatherForMultipleDays(String location, int days) {
        logger.info("Fetching weather forecast for {} for the next {} days", location, days);
        try {
            String url = String.format("/forecast.json?key=%s&q=%s&days=%d&aqi=yes", apiKey, location, days);

            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null || !response.has(FORECAST) || 
                !response.get(FORECAST).has("forecastday") || 
                response.get(FORECAST).get("forecastday").isEmpty()) {
                logger.error("Empty forecast response");
                return new ArrayList<>();
            }

            List<WeatherData> forecast = new ArrayList<>();
            JsonNode forecastDays = response.get(FORECAST).get("forecastday");
            for (int i = 0; i < forecastDays.size(); i++) {
                JsonNode forecastDay = forecastDays.get(i);
                String dateStr = safeGetString(forecastDay, "date");
                if (dateStr == null) continue;
                
                LocalDate date = LocalDate.parse(dateStr);
                JsonNode day = forecastDay.get("day");
                if (day == null) continue;
                
                Double temp = safeGetDouble(day, "avgtemp_c");
                Double feelsLike = safeGetDouble(day, "avgfeelslike_c");
                Double maxTemp = safeGetDouble(day, "maxtemp_c");
                Double minTemp = safeGetDouble(day, "mintemp_c");
                String desc = safeGetNestedString(day, "condition", "text");
                Integer hum = safeGetInt(day, "avghumidity");
                Double wind = safeGetDouble(day, "maxwind_kph");
                
                String windDir = null;
                windDir = safeGetString(day, "wind_dir");
                
                // If not available, try getting from the first hour of the forecast
                if (windDir == null && forecastDay.has("hour") && !forecastDay.get("hour").isEmpty()) {
                    JsonNode firstHour = forecastDay.get("hour").get(0);
                    windDir = safeGetString(firstHour, "wind_dir");
                }
                
                // If still not available and it's the first day, try getting from current conditions
                if (windDir == null && i == 0 && response.has("current") && 
                    response.get("current").has("wind_dir")) {
                    windDir = safeGetString(response.get("current"), "wind_dir");
                }
                
                Double precip = safeGetDouble(day, "totalprecip_mm");
                Integer rainChance = safeGetInt(day, "daily_chance_of_rain");
                Double uv = safeGetDouble(day, "uv");
                
                JsonNode astroNode = forecastDay.has("astro") ? forecastDay.get("astro") : null;
                String sunrise = astroNode != null ? safeGetString(astroNode, "sunrise") : null;
                String sunset = astroNode != null ? safeGetString(astroNode, "sunset") : null;
                
                // Get weather icon
                String icon = safeGetNestedString(day, "condition", "icon");

                WeatherData weatherData = new WeatherData(
                        date.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        location,
                        temp,
                        feelsLike,
                        maxTemp, minTemp,
                        desc != null ? desc : "No description available",
                        hum,
                        wind,
                        windDir,
                        precip,
                        rainChance,
                        uv,
                        sunrise,
                        sunset,
                        icon
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

    private Double safeGetDouble(JsonNode node, String field) {
        if (node != null && node.has(field) && !node.get(field).isNull()) {
            try {
                return node.get(field).asDouble();
            } catch (Exception e) {
                logger.warn("Failed to parse double value for field {}: {}", field, e.getMessage());
            }
        }
        return null;
    }

    private Integer safeGetInt(JsonNode node, String field) {
        if (node != null && node.has(field) && !node.get(field).isNull()) {
            try {
                return node.get(field).asInt();
            } catch (Exception e) {
                logger.warn("Failed to parse integer value for field {}: {}", field, e.getMessage());
            }
        }
        return null;
    }

    private String safeGetString(JsonNode node, String field) {
        if (node != null && node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asText();
        }
        return null;
    }

    private String safeGetNestedString(JsonNode node, String parentField, String childField) {
        if (node != null && node.has(parentField) && !node.get(parentField).isNull() && 
            node.get(parentField).has(childField) && !node.get(parentField).get(childField).isNull()) {
            return node.get(parentField).get(childField).asText();
        }
        return null;
    }
}