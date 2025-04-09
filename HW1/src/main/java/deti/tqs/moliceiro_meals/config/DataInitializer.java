package deti.tqs.moliceiro_meals.config;

import deti.tqs.moliceiro_meals.model.*;
import deti.tqs.moliceiro_meals.repository.MenuItemRepository;
import deti.tqs.moliceiro_meals.repository.MenuRepository;
import deti.tqs.moliceiro_meals.repository.ReservationRepository;
import deti.tqs.moliceiro_meals.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private static final String RESTAURANT_MOLICEIRO = "Restaurant Do Moliceiro";
    private static final String RESTAURANT_SALINEIRA = "A Salineira";
    private static final String RESTAURANT_RIAVISTA = "Riavista";
    private static final String RESTAURANT_CERVEJARIA_ROSSIO = "Cervejaria Rossio";

    @Bean
    @Profile("!test")
    public CommandLineRunner initializeData(
            RestaurantRepository restaurantRepository,
            MenuRepository menuRepository,
            MenuItemRepository menuItemRepository,
            ReservationRepository reservationRepository) {
        
        return args -> {
            logger.info("Starting data initialization...");
            
            if (restaurantRepository.count() > 0) {
                logger.info("Data already exists, skipping initialization");
                return;
            }

            List<Restaurant> restaurants = new ArrayList<>();
            
            // Restaurant 1
            Restaurant restaurant1 = new Restaurant(
                RESTAURANT_MOLICEIRO, 
                "Aveiro",
                "A modern Portuguese restaurant with a cozy atmosphere, serving traditional dishes with a contemporary twist.",
                "234 567 890"
            );
            restaurants.add(restaurant1);
            
            // Restaurant 2
            Restaurant restaurant2 = new Restaurant(
                RESTAURANT_SALINEIRA,
                "Aveiro Canal Central",
                "Set in a historic salt warehouse, this upscale restaurant offers innovative cuisine using local ingredients.",
                "234 382 674"
            );
            restaurants.add(restaurant2);
            
            // Restaurant 3
            Restaurant restaurant3 = new Restaurant(
                RESTAURANT_RIAVISTA,
                "Rua da Estação",
                "A vibrant tapas bar with an extensive wine list and a lively atmosphere.",
                "234 483 926"
            );
            restaurants.add(restaurant3);
            
            // Restaurant 4
            Restaurant restaurant4 = new Restaurant(
                RESTAURANT_CERVEJARIA_ROSSIO, 
                "Praça do Rossio",
                "A traditional beer house specializing in fresh seafood and draft beers.",
                "234 912 154"
            );
            restaurants.add(restaurant4);
            
            restaurantRepository.saveAll(restaurants);
            logger.debug("Created and saved {} restaurants", restaurants.size());
            
            // Create menus for each restaurant (today and upcoming days)
            createMenusForRestaurant(restaurant1, menuRepository, menuItemRepository);
            createMenusForRestaurant(restaurant2, menuRepository, menuItemRepository);
            createMenusForRestaurant(restaurant3, menuRepository, menuItemRepository);
            createMenusForRestaurant(restaurant4, menuRepository, menuItemRepository);
            
            // Create sample reservations
            createSampleReservations(restaurants, reservationRepository);
            
            logger.info("Data initialization completed successfully");
        };
    }
    
    private void createMenusForRestaurant(
            Restaurant restaurant, 
            MenuRepository menuRepository,
            MenuItemRepository menuItemRepository) {
        
        logger.debug("Creating menus for restaurant: {}", restaurant.getName());
        
        LocalDate today = LocalDate.now();
        
        // Create today's menus
        Menu todayLunchMenu = createMenu("Daily Lunch Special", 
            "Our chef's lunch selection featuring fresh and light meals", 
            today, restaurant);
        menuRepository.save(todayLunchMenu);
        
        Menu todayDinnerMenu = createMenu("Evening Special Menu", 
            "Elegant dinner options prepared with seasonal ingredients", 
            today, restaurant);
        menuRepository.save(todayDinnerMenu);
        
        // Create menu items for today's lunch
        List<MenuItem> todayLunchItems = createTodayLunchItems(restaurant, todayLunchMenu);
        menuItemRepository.saveAll(todayLunchItems);
        logger.debug("Created {} items for lunch menu", todayLunchItems.size());
        
        // Create menu items for today's dinner
        List<MenuItem> todayDinnerItems = createTodayDinnerItems(restaurant, todayDinnerMenu);
        menuItemRepository.saveAll(todayDinnerItems);
        
        // Create menus for upcoming days
        createFutureMenus(restaurant, today, menuRepository, menuItemRepository);
        
        logger.debug("Created future menus for restaurant: {}", restaurant.getName());
    }

    private Menu createMenu(String name, String description, LocalDate date, Restaurant restaurant) {
        return new Menu(name, description, date, restaurant);
    }

    private List<MenuItem> createTodayLunchItems(Restaurant restaurant, Menu todayLunchMenu) {
        List<MenuItem> todayLunchItems = new ArrayList<>();
        
        if (restaurant.getName().equals(RESTAURANT_MOLICEIRO)) {
            // Traditional Portuguese lunch
            todayLunchItems.add(new MenuItem("Caldo Verde", "Traditional Portuguese kale soup with chorizo", new BigDecimal("4.50"), MenuItemType.APPETIZER, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Bacalhau à Gomes de Sá", "Codfish with potatoes, eggs, and olives", new BigDecimal("12.75"), MenuItemType.MAIN_COURSE, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Roasted Chicken", "Farm chicken with piri-piri sauce", new BigDecimal("9.95"), MenuItemType.MAIN_COURSE, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Arroz Doce", "Sweet rice pudding with cinnamon", new BigDecimal("3.95"), MenuItemType.DESSERT, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Fresh Orange Juice", "Squeezed daily", new BigDecimal("2.75"), MenuItemType.BEVERAGE, todayLunchMenu));
        } else if (restaurant.getName().equals(RESTAURANT_SALINEIRA)) {
            // Upscale lunch
            todayLunchItems.add(new MenuItem("Oysters", "Fresh local oysters with lemon", new BigDecimal("14.50"), MenuItemType.APPETIZER, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Lobster Bisque", "Creamy soup with lobster pieces", new BigDecimal("9.95"), MenuItemType.APPETIZER, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Sea Bass Fillet", "Grilled with herbs and olive oil", new BigDecimal("18.50"), MenuItemType.MAIN_COURSE, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Chocolate Mousse", "Dark chocolate with sea salt", new BigDecimal("7.95"), MenuItemType.DESSERT, todayLunchMenu));
            todayLunchItems.add(new MenuItem("White Wine Sangria", "Made with local white wine", new BigDecimal("6.50"), MenuItemType.BEVERAGE, todayLunchMenu));
        } else if (restaurant.getName().equals(RESTAURANT_RIAVISTA)) {
            // Tapas for lunch
            todayLunchItems.add(new MenuItem("Patatas Bravas", "Fried potatoes with spicy sauce", new BigDecimal("4.95"), MenuItemType.APPETIZER, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Gambas al Ajillo", "Garlic shrimp", new BigDecimal("9.50"), MenuItemType.APPETIZER, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Chorizo in Wine", "Sautéed chorizo in red wine", new BigDecimal("7.95"), MenuItemType.MAIN_COURSE, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Churros", "Served with chocolate sauce", new BigDecimal("5.50"), MenuItemType.DESSERT, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Red Sangria", "House special", new BigDecimal("5.95"), MenuItemType.BEVERAGE, todayLunchMenu));
        } else { // Cervejaria Rossio
            // Beer house lunch
            todayLunchItems.add(new MenuItem("Clams à Bulhão Pato", "Clams cooked with garlic and cilantro", new BigDecimal("11.95"), MenuItemType.APPETIZER, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Shrimp Skewers", "Grilled with lemon and herbs", new BigDecimal("9.50"), MenuItemType.APPETIZER, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Fish and Chips", "Cod with homemade fries", new BigDecimal("10.95"), MenuItemType.MAIN_COURSE, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Ice Cream", "Assorted flavors", new BigDecimal("4.50"), MenuItemType.DESSERT, todayLunchMenu));
            todayLunchItems.add(new MenuItem("Craft Beer", "Selection of local brews", new BigDecimal("4.95"), MenuItemType.BEVERAGE, todayLunchMenu));
        }
        
        return todayLunchItems;
    }

    private List<MenuItem> createTodayDinnerItems(Restaurant restaurant, Menu todayDinnerMenu) {
        List<MenuItem> todayDinnerItems = new ArrayList<>();
        
        if (restaurant.getName().equals(RESTAURANT_MOLICEIRO)) {
            // Traditional Portuguese dinner
            todayDinnerItems.add(new MenuItem("Cheese and Charcuterie Board", "Selection of regional cheeses and cured meats", new BigDecimal("13.95"), MenuItemType.APPETIZER, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Grilled Sardines", "Fresh sardines with olive oil and herbs", new BigDecimal("12.50"), MenuItemType.MAIN_COURSE, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Octopus Rice", "Slow-cooked octopus with rice", new BigDecimal("14.75"), MenuItemType.MAIN_COURSE, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Crème Brûlée", "Traditional French dessert", new BigDecimal("5.50"), MenuItemType.DESSERT, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Port Wine", "Aged tawny", new BigDecimal("7.50"), MenuItemType.BEVERAGE, todayDinnerMenu));
        } else if (restaurant.getName().equals(RESTAURANT_SALINEIRA)) {
            // Upscale dinner
            todayDinnerItems.add(new MenuItem("Foie Gras", "With fig jam and brioche", new BigDecimal("16.95"), MenuItemType.APPETIZER, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Truffle Risotto", "Arborio rice with black truffle", new BigDecimal("19.50"), MenuItemType.MAIN_COURSE, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Wagyu Beef", "Premium beef with roasted vegetables", new BigDecimal("32.95"), MenuItemType.MAIN_COURSE, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Crème Caramel", "Traditional custard with caramel", new BigDecimal("8.50"), MenuItemType.DESSERT, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Espresso Martini", "Coffee-based cocktail", new BigDecimal("9.95"), MenuItemType.BEVERAGE, todayDinnerMenu));
        } else if (restaurant.getName().equals(RESTAURANT_RIAVISTA)) {
            // Evening tapas
            todayDinnerItems.add(new MenuItem("Iberico Ham", "Premium cured ham", new BigDecimal("15.95"), MenuItemType.APPETIZER, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Stuffed Peppers", "Peppers filled with goat cheese", new BigDecimal("8.50"), MenuItemType.APPETIZER, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Seafood Paella", "Traditional Spanish rice dish", new BigDecimal("22.95"), MenuItemType.MAIN_COURSE, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Crema Catalana", "Citrus-infused custard", new BigDecimal("6.50"), MenuItemType.DESSERT, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Rioja Wine", "Selected Spanish red", new BigDecimal("7.95"), MenuItemType.BEVERAGE, todayDinnerMenu));
        } else { // Cervejaria Rossio
            // Beer house dinner
            todayDinnerItems.add(new MenuItem("Seafood Platter", "Assortment of fresh seafood", new BigDecimal("24.95"), MenuItemType.APPETIZER, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Grilled Tiger Prawns", "With garlic butter", new BigDecimal("18.95"), MenuItemType.MAIN_COURSE, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Mixed Grill", "Selection of meats with fries", new BigDecimal("19.50"), MenuItemType.MAIN_COURSE, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Chocolate Cake", "Rich with vanilla ice cream", new BigDecimal("6.95"), MenuItemType.DESSERT, todayDinnerMenu));
            todayDinnerItems.add(new MenuItem("Premium Beer Flight", "Tasting of 4 craft beers", new BigDecimal("8.95"), MenuItemType.BEVERAGE, todayDinnerMenu));
        }
        
        return todayDinnerItems;
    }

    private void createFutureMenus(
            Restaurant restaurant, 
            LocalDate today,
            MenuRepository menuRepository,
            MenuItemRepository menuItemRepository) {
        
        for (int i = 1; i <= 7; i++) {
            LocalDate futureDate = today.plusDays(i);
            String dayName = futureDate.getDayOfWeek().toString().toLowerCase();
            dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
            
            // Create and save future lunch menu
            Menu futureLunchMenu = createMenu(
                dayName + " Lunch Special", 
                "Our chef's selection for " + dayName.toLowerCase() + " lunch", 
                futureDate, restaurant);
            menuRepository.save(futureLunchMenu);
            
            // Create and save future dinner menu
            Menu futureDinnerMenu = createMenu(
                dayName + " Dinner Delight", 
                "Evening specialties for " + dayName.toLowerCase(), 
                futureDate, restaurant);
            menuRepository.save(futureDinnerMenu);
            
            // Create and save future lunch items
            List<MenuItem> futureLunchItems = createFutureLunchItems(i, futureLunchMenu);
            menuItemRepository.saveAll(futureLunchItems);
            
            // Create and save future dinner items
            List<MenuItem> futureDinnerItems = createFutureDinnerItems(i, futureDinnerMenu);
            menuItemRepository.saveAll(futureDinnerItems);
        }
    }

    private List<MenuItem> createFutureLunchItems(int dayOffset, Menu futureLunchMenu) {
        List<MenuItem> futureLunchItems = new ArrayList<>();
        
        if (dayOffset % 2 == 0) { // Even days
            futureLunchItems.add(new MenuItem("Tomato Soup", "Fresh tomato with basil", new BigDecimal("4.95"), MenuItemType.APPETIZER, futureLunchMenu));
            futureLunchItems.add(new MenuItem("Grilled Chicken Salad", "Mixed greens with grilled chicken", new BigDecimal("9.95"), MenuItemType.MAIN_COURSE, futureLunchMenu));
            futureLunchItems.add(new MenuItem("Vegetable Quiche", "Seasonal vegetables in pastry", new BigDecimal("8.50"), MenuItemType.MAIN_COURSE, futureLunchMenu));
            futureLunchItems.add(new MenuItem("Fruit Salad", "Fresh seasonal fruits", new BigDecimal("4.50"), MenuItemType.DESSERT, futureLunchMenu));
        } else { // Odd days
            futureLunchItems.add(new MenuItem("Gazpacho", "Cold vegetable soup", new BigDecimal("5.50"), MenuItemType.APPETIZER, futureLunchMenu));
            futureLunchItems.add(new MenuItem("Fish of the Day", "Grilled with lemon butter", new BigDecimal("13.95"), MenuItemType.MAIN_COURSE, futureLunchMenu));
            futureLunchItems.add(new MenuItem("Pasta Primavera", "With seasonal vegetables", new BigDecimal("10.50"), MenuItemType.MAIN_COURSE, futureLunchMenu));
            futureLunchItems.add(new MenuItem("Chocolate Brownie", "With vanilla ice cream", new BigDecimal("5.95"), MenuItemType.DESSERT, futureLunchMenu));
        }
        
        return futureLunchItems;
    }

    private List<MenuItem> createFutureDinnerItems(int dayOffset, Menu futureDinnerMenu) {
        List<MenuItem> futureDinnerItems = new ArrayList<>();
        
        int remainder = dayOffset % 3;
        
        if (remainder == 0) { // Every third day
            futureDinnerItems.add(new MenuItem("Stuffed Mushrooms", "With herb cream cheese", new BigDecimal("7.95"), MenuItemType.APPETIZER, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Prime Rib", "Slow-roasted with jus", new BigDecimal("24.95"), MenuItemType.MAIN_COURSE, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Seafood Linguine", "Mixed seafood in white wine sauce", new BigDecimal("18.50"), MenuItemType.MAIN_COURSE, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Tiramisu", "Classic Italian dessert", new BigDecimal("6.95"), MenuItemType.DESSERT, futureDinnerMenu));
        } else if (remainder == 1) { // Remainder 1
            futureDinnerItems.add(new MenuItem("Shrimp Cocktail", "With homemade sauce", new BigDecimal("9.95"), MenuItemType.APPETIZER, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Duck Confit", "With orange sauce", new BigDecimal("22.50"), MenuItemType.MAIN_COURSE, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Vegetable Curry", "Aromatic with basmati rice", new BigDecimal("15.95"), MenuItemType.MAIN_COURSE, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Cheesecake", "New York style", new BigDecimal("7.50"), MenuItemType.DESSERT, futureDinnerMenu));
        } else { // Remainder 2
            futureDinnerItems.add(new MenuItem("Tuna Tartare", "With avocado and ponzu", new BigDecimal("12.95"), MenuItemType.APPETIZER, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Lamb Chops", "With mint sauce", new BigDecimal("26.50"), MenuItemType.MAIN_COURSE, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Eggplant Parmesan", "Baked with marinara", new BigDecimal("14.95"), MenuItemType.MAIN_COURSE, futureDinnerMenu));
            futureDinnerItems.add(new MenuItem("Crème Brûlée", "With caramelized sugar", new BigDecimal("6.95"), MenuItemType.DESSERT, futureDinnerMenu));
        }
        
        return futureDinnerItems;
    }
    
    private void createSampleReservations(List<Restaurant> restaurants, ReservationRepository reservationRepository) {
        logger.debug("Creating sample reservations");
        
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
        logger.info("Created {} sample reservations", reservations.size());
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