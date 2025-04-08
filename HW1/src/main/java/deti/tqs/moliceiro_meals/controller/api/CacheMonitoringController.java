package deti.tqs.moliceiro_meals.controller.api;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import deti.tqs.moliceiro_meals.service.WeatherService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cache")
public class CacheMonitoringController {

    private final CacheManager cacheManager;
    private final WeatherService weatherService;

    public CacheMonitoringController(CacheManager cacheManager, WeatherService weatherService) {
        this.cacheManager = cacheManager;
        this.weatherService = weatherService;
    }

    @GetMapping("/stats")
    public Map<String, Integer> getCacheStats() {
        return weatherService.getCacheStats();
    }

    @GetMapping("/contents")
    public Map<String, Object> getCacheContents() {
        Map<String, Object> contents = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Object nativeCache = cache.getNativeCache();
                if (nativeCache instanceof Map<?, ?>) {
                    Map<?, ?> cacheMap = (Map<?, ?>) nativeCache;
                    contents.put(cacheName, cacheMap.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> entry.getKey().toString(),
                                    entry -> entry.getValue().toString()
                            )));
                } else {
                    contents.put(cacheName, "Unsupported cache type: " + nativeCache.getClass().getName());
                }
            }
        });

        return contents;
    }
}
