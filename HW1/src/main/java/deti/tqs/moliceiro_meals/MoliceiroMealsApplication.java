package deti.tqs.moliceiro_meals;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoliceiroMealsApplication {

    public static void main(String[] args) {
        // Load environment variables from .env
        Dotenv dotenv = Dotenv.configure().load();

        // Start the Spring Boot application
        SpringApplication.run(MoliceiroMealsApplication.class, args);
    }
}
