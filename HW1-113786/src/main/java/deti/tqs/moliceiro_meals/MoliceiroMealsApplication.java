package deti.tqs.moliceiro_meals;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MoliceiroMealsApplication {

    public static void main(String[] args) {
        // Load environment variables from .env
        Dotenv dotenv = Dotenv.configure().load();

        // Set system properties for Spring Boot
        System.setProperty("WEATHER_API_KEY", dotenv.get("WEATHER_API_KEY"));
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_DRIVER", dotenv.get("DB_DRIVER"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(MoliceiroMealsApplication.class, args);
    }
}
