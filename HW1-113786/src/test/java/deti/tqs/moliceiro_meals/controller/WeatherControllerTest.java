package deti.tqs.moliceiro_meals.controller;

import deti.tqs.moliceiro_meals.controller.api.WeatherController;
import deti.tqs.moliceiro_meals.model.WeatherData;
import deti.tqs.moliceiro_meals.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void testGetWeatherForecast() throws Exception {
        // Create a mock weather data
        WeatherData mockData = new WeatherData(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
            "Aveiro", 18.5, "Sunny", 70, 10.0
        );
        
        // Mock the service response for the tracking method
        when(weatherService.getWeatherForecastWithTracking("Aveiro", LocalDate.now()))
            .thenReturn(mockData);

        // Perform the GET request
        mockMvc.perform(get("/api/weather/Aveiro?date=" + LocalDate.now()))
            .andExpect(status().isOk());
    }

    @Test
    void testGetWeatherForecastInvalidLocation() throws Exception {
        // Mock the service response for the tracking method with an invalid location
        WeatherData errorData = new WeatherData(
            Instant.now(), "InvalidLocation", null, "Error fetching data", null, null
        );
        
        when(weatherService.getWeatherForecastWithTracking("InvalidLocation", LocalDate.now()))
            .thenReturn(errorData);

        // Perform the GET request
        mockMvc.perform(get("/api/weather/InvalidLocation?date=" + LocalDate.now()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.location").value("InvalidLocation"))
            .andExpect(jsonPath("$.description").value("Error fetching data"));
    }

    @Test
    void testGetForecastForNextDays() throws Exception {
        // Create a mock forecast list
        List<WeatherData> mockForecast = List.of(
            new WeatherData(LocalDate.of(2025, 4, 5).atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                            "Aveiro", 12.2, "Heavy rain", 89, 37.4),
            new WeatherData(LocalDate.of(2025, 4, 6).atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                            "Aveiro", 14.5, "Cloudy", 75, 20.0),
            new WeatherData(LocalDate.of(2025, 4, 7).atStartOfDay(ZoneId.systemDefault()).toInstant(), 
                            "Aveiro", 16.0, "Sunny", 50, 15.0)
        );
        
        // Mock the service response for the tracking method
        when(weatherService.getForecastForNextDaysWithTracking("Aveiro", 3))
            .thenReturn(mockForecast);

        // Perform the GET request
        mockMvc.perform(get("/api/weather/Aveiro/forecast?days=3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].location").value("Aveiro"))
            .andExpect(jsonPath("$[0].description").value("Heavy rain"));
    }

    @Test
    void testGetForecastForNextDaysEmptyResponse() throws Exception {
        // Mock the service response
        when(weatherService.getForecastForNextDays("Aveiro", 3)).thenReturn(List.of());

        // Perform the GET request
        mockMvc.perform(get("/api/weather/Aveiro/forecast?days=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetWeatherForecastInvalidDateFormat() throws Exception {
        // Perform the GET request with an invalid date
        mockMvc.perform(get("/api/weather/Aveiro?date=invalid-date"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Invalid date format. Please use 'yyyy-MM-dd'."));
    }
}