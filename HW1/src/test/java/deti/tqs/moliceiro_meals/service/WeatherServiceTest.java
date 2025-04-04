package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.model.WeatherData;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class WeatherServiceTest {

    private WeatherService weatherService;
    private WebClient.Builder webClientBuilderMock;

    @BeforeEach
    void setUp() {
        webClientBuilderMock = Mockito.mock(WebClient.Builder.class);
        WebClient webClientMock = Mockito.mock(WebClient.class);
        when(webClientBuilderMock.baseUrl(any(String.class))).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);

        weatherService = new WeatherService(webClientBuilderMock);
    }

    @Test
    void testGetWeatherForecast() throws Exception {
        // Mock the API response
        WebClient.RequestHeadersUriSpec requestMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);

        when(webClientBuilderMock.build().get()).thenReturn(requestMock);
        when(requestMock.uri(any(String.class))).thenReturn(headersMock);
        when(headersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(eq(JsonNode.class))).thenReturn(Mono.just(createMockResponse()));

        // Call the method
        WeatherData weatherData = weatherService.getWeatherForecast("Aveiro", LocalDate.now());

        // Assertions
        assertNotNull(weatherData);
        assertEquals("Aveiro", weatherData.getLocation());
        assertEquals("Heavy rain", weatherData.getDescription());
    }

    @Test
    void testGetWeatherForecastWithInvalidLocation() {
        // Mock the API response to throw an exception
        when(webClientBuilderMock.build().get()).thenThrow(new RuntimeException("Invalid location"));

        // Call the method
        WeatherData weatherData = weatherService.getWeatherForecast("InvalidLocation", LocalDate.now());

        // Assertions
        assertNotNull(weatherData);
        assertEquals("InvalidLocation", weatherData.getLocation());
        assertEquals("Error fetching data", weatherData.getDescription());
    }

    @Test
    void testGetWeatherForecastWithInvalidDate() {
        // Call the method with an invalid date
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherForecast("Aveiro", LocalDate.parse("invalid-date"));
        });

        // Assertions
        assertEquals("Text 'invalid-date' could not be parsed at index 0", exception.getMessage());
    }

    @Test
    void testGetForecastForNextDays() {
        // Mock the API response
        WebClient.RequestHeadersUriSpec requestMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);

        when(webClientBuilderMock.build().get()).thenReturn(requestMock);
        when(requestMock.uri(any(String.class))).thenReturn(headersMock);
        when(headersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(eq(JsonNode.class))).thenReturn(Mono.just(createMockResponse()));

        // Call the method
        var forecast = weatherService.getForecastForNextDays("Aveiro", 3);
        System.out.println(forecast);

        // Assertions
        assertNotNull(forecast);
        assertEquals(3, forecast.size());
        assertEquals("Aveiro", forecast.get(0).getLocation());
    }

    @Test
    void testGetForecastForNextDaysEmptyResponse() {
        // Mock the API response with an empty forecast
        WebClient.RequestHeadersUriSpec requestMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);

        when(webClientBuilderMock.build().get()).thenReturn(requestMock);
        when(requestMock.uri(any(String.class))).thenReturn(headersMock);
        when(headersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(eq(JsonNode.class))).thenReturn(Mono.just(createEmptyMockResponse()));

        // Call the method
        var forecast = weatherService.getForecastForNextDays("Aveiro", 3);

        // Assertions
        assertNotNull(forecast);
        assertTrue(forecast.isEmpty(), "Forecast list should be empty for an empty API response");
    }

    @Test
    void testGetWeatherForecastApiError() {
        // Mock the API response to throw an exception
        WebClient.RequestHeadersUriSpec requestMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);

        when(webClientBuilderMock.build().get()).thenReturn(requestMock);
        when(requestMock.uri(any(String.class))).thenReturn(headersMock);
        when(headersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(eq(JsonNode.class))).thenThrow(new RuntimeException("API error"));

        // Call the method
        WeatherData weatherData = weatherService.getWeatherForecast("Aveiro", LocalDate.now());

        // Assertions
        assertNotNull(weatherData);
        assertEquals("Aveiro", weatherData.getLocation());
        assertEquals("Error fetching data", weatherData.getDescription());
    }

    private JsonNode createMockResponse() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = """
            {
                "location": { "name": "Aveiro" },
                "forecast": {
                    "forecastday": [
                        {
                            "date": "2025-04-04",
                            "day": {
                                "avgtemp_c": 12.2,
                                "condition": { "text": "Heavy rain" },
                                "avghumidity": 89,
                                "maxwind_kph": 37.4
                            }
                        },
                        {
                            "date": "2025-04-05",
                            "day": {
                                "avgtemp_c": 14.5,
                                "condition": { "text": "Cloudy" },
                                "avghumidity": 75,
                                "maxwind_kph": 20.0
                            }
                        },
                        {
                            "date": "2025-04-06",
                            "day": {
                                "avgtemp_c": 16.0,
                                "condition": { "text": "Sunny" },
                                "avghumidity": 50,
                                "maxwind_kph": 15.0
                            }
                        }
                    ]
                }
            }
        """;
        try {
            return mapper.readTree(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock response", e);
        }
    }

    private JsonNode createEmptyMockResponse() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = """
            {
                "location": { "name": "Aveiro" },
                "forecast": {
                    "forecastday": []
                }
            }
        """;
        try {
            return mapper.readTree(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error creating mock response", e);
        }
    }
}
