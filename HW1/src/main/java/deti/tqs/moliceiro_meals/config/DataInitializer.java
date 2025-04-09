package deti.tqs.moliceiro_meals.config;

import deti.tqs.moliceiro_meals.model.*;
import deti.tqs.moliceiro_meals.repository.MenuItemRepository;
import deti.tqs.moliceiro_meals.repository.MenuRepository;
import deti.tqs.moliceiro_meals.repository.ReservationRepository;
import deti.tqs.moliceiro_meals.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test") // Don't run this during testing
    public CommandLineRunner initializeData(
            RestaurantRepository restaurantRepository,
            MenuRepository menuRepository,
            MenuItemRepository menuItemRepository,
            ReservationRepository reservationRepository) {
        
        return args -> {
            System.out.println("Starting data initialization...");
            
            // Check if data already exists (to avoid duplicating on restarts)
            if (restaurantRepository.count() > 0) {
                System.out.println("Data already exists, skipping initialization");
                return; // Data already exists, skip initialization
            }

            // Create example restaurants
            List<Restaurant> restaurants = new ArrayList<>();
            
            // Restaurant 1
            Restaurant restaurant1 = new Restaurant(
                "O Bairro", 
                "Aveiro",
                "A modern Portuguese restaurant with a cozy atmosphere, serving traditional dishes with a contemporary twist.",
                "234 567 890"
            );
            restaurants.add(restaurant1);
            
            // Restaurant 2
            Restaurant restaurant2 = new Restaurant(
                "Salpoente",
                "Aveiro Canal Central",
                "Set in a historic salt warehouse, this upscale restaurant offers innovative cuisine using local ingredients.",
                "234 382 674"
            );
            restaurants.add(restaurant2);
            
            // Restaurant 3
            Restaurant restaurant3 = new Restaurant(
                "Ramona",
                "Rua da Estação",
                "A vibrant tapas bar with an extensive wine list and a lively atmosphere.",
                "234 483 926"
            );
            restaurants.add(restaurant3);
            
            // Restaurant 4
            Restaurant restaurant4 = new Restaurant(
                "Cervejaria Rossio", 
                "Praça do Rossio",
                "A traditional beer house specializing in fresh seafood and draft beers.",
                "234 912 154"
            );
            restaurants.add(restaurant4);
            
            // Save restaurants
            restaurantRepository.saveAll(restaurants);
            
            // Create menus for each restaurant (today and upcoming days)
            createMenusForRestaurant(restaurant1, menuRepository, menuItemRepository);
            createMenusForRestaurant(restaurant2, menuRepository, menuItemRepository);
            createMenusForRestaurant(restaurant3, menuRepository, menuItemRepository);
            createMenusForRestaurant(restaurant4, menuRepository, menuItemRepository);
            
            // Create sample reservations
            createSampleReservations(restaurants, reservationRepository);
            
            System.out.println("Data initialization completed successfully");
        };
    }
    
    private void createMenusForRestaurant(
            Restaurant restaurant, 
            MenuRepository menuRepository,
            MenuItemRepository menuItemRepository) {
        
        LocalDate today = LocalDate.now();
        
        // Create today's menu
        Menu todayMenu = new Menu(
            "Daily Special Menu", 
            "Our chef's selection for today", 
            today, 
            restaurant
        );
        menuRepository.save(todayMenu);
        
        // Add menu items to today's menu
        List<MenuItem> todayItems = new ArrayList<>();
        todayItems.add(new MenuItem("Grilled Sardines", "Fresh sardines with olive oil and herbs", new BigDecimal("12.50"), MenuItemType.MAIN_COURSE, todayMenu));
        todayItems.add(new MenuItem("Octopus Rice", "Slow-cooked octopus with rice", new BigDecimal("14.75"), MenuItemType.MAIN_COURSE, todayMenu));
        todayItems.add(new MenuItem("Cheese Platter", "Selection of regional cheeses", new BigDecimal("8.95"), MenuItemType.APPETIZER, todayMenu));
        todayItems.add(new MenuItem("Crème Brûlée", "Traditional French dessert", new BigDecimal("5.50"), MenuItemType.DESSERT, todayMenu));
        menuItemRepository.saveAll(todayItems);
        
        // Create menus for upcoming days
        for (int i = 1; i <= 7; i++) {
            LocalDate futureDate = today.plusDays(i);
            Menu futureMenu = new Menu(
                "Menu for " + futureDate.getDayOfWeek().toString(), 
                "Special menu for " + futureDate.getDayOfWeek().toString().toLowerCase(), 
                futureDate, 
                restaurant
            );
            menuRepository.save(futureMenu);
            
            // Add menu items for future menus
            List<MenuItem> futureItems = new ArrayList<>();
            futureItems.add(new MenuItem("Bacalhau à Brás", "Shredded cod with onions and fries", new BigDecimal("13.95"), MenuItemType.MAIN_COURSE, futureMenu));
            futureItems.add(new MenuItem("Mushroom Risotto", "Creamy rice with wild mushrooms", new BigDecimal("11.50"), MenuItemType.MAIN_COURSE, futureMenu));
            futureItems.add(new MenuItem("Green Salad", "Fresh greens with vinaigrette", new BigDecimal("4.95"), MenuItemType.APPETIZER, futureMenu));
            futureItems.add(new MenuItem("Pastel de Nata", "Traditional Portuguese custard tart", new BigDecimal("2.50"), MenuItemType.DESSERT, futureMenu));
            menuItemRepository.saveAll(futureItems);
        }
    }
    
    private void createSampleReservations(List<Restaurant> restaurants, ReservationRepository reservationRepository) {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = new ArrayList<>();
        
        // Example reservations for today
        reservations.add(createReservation(
            "João Silva", 
            "joao.silva@email.com", 
            "912345678", 
            4, 
            now.with(LocalTime.of(19, 30)), 
            "Window table if possible", 
            restaurants.get(0), 
            ReservationStatus.CONFIRMED
        ));
        
        reservations.add(createReservation(
            "Maria Santos", 
            "maria.santos@email.com", 
            "962345678", 
            2, 
            now.with(LocalTime.of(20, 15)), 
            "Anniversary celebration", 
            restaurants.get(1), 
            ReservationStatus.CONFIRMED
        ));
        
        // Example reservations for tomorrow
        LocalDateTime tomorrow = now.plusDays(1);
        reservations.add(createReservation(
            "António Costa", 
            "antonio.costa@email.com", 
            "932345678", 
            6, 
            tomorrow.with(LocalTime.of(13, 0)), 
            "Business lunch", 
            restaurants.get(2), 
            ReservationStatus.PENDING
        ));
        
        // Example reservations for next weekend
        LocalDateTime nextWeekend = now.plusDays(5);
        reservations.add(createReservation(
            "Sofia Ferreira", 
            "sofia.ferreira@email.com", 
            "922345678", 
            8, 
            nextWeekend.with(LocalTime.of(20, 0)), 
            "Birthday party", 
            restaurants.get(3), 
            ReservationStatus.PENDING
        ));
        
        // Add some cancelled reservations
        reservations.add(createReservation(
            "Carlos Mendes", 
            "carlos.mendes@email.com", 
            "952345678", 
            3, 
            now.minusDays(1).with(LocalTime.of(19, 0)), 
            "", 
            restaurants.get(0), 
            ReservationStatus.CANCELLED
        ));
        
        // Add some completed reservations in the past
        reservations.add(createReservation(
            "Ana Rodrigues", 
            "ana.rodrigues@email.com", 
            "972345678", 
            4, 
            now.minusDays(3).with(LocalTime.of(20, 30)), 
            "Gluten-free options needed", 
            restaurants.get(1), 
            ReservationStatus.COMPLETED
        ));
        
        // Save all reservations
        reservationRepository.saveAll(reservations);
        System.out.println("Created " + reservations.size() + " sample reservations");
    }
    
    private Reservation createReservation(
            String customerName, 
            String customerEmail, 
            String customerPhone, 
            int partySize, 
            LocalDateTime reservationTime, 
            String specialRequests, 
            Restaurant restaurant, 
            ReservationStatus status) {
        
        Reservation reservation = new Reservation();
        reservation.setCustomerName(customerName);
        reservation.setCustomerEmail(customerEmail);
        reservation.setCustomerPhone(customerPhone);
        reservation.setPartySize(partySize);
        reservation.setReservationTime(reservationTime);
        reservation.setSpecialRequests(specialRequests);
        reservation.setRestaurant(restaurant);
        reservation.setStatus(status);
        reservation.setToken(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setCreatedAt(LocalDateTime.now());
        
        return reservation;
    }
}