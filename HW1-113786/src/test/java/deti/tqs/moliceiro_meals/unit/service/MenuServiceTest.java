package deti.tqs.moliceiro_meals.unit.service;

import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.MenuItemService;
import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.repository.MenuRepository;
import deti.tqs.moliceiro_meals.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuItemRepository menuItemRepository;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuRepository = mock(MenuRepository.class);
        menuItemRepository = mock(MenuItemRepository.class);
        menuService = new MenuService(menuRepository, menuItemRepository, new MenuItemService());
    }

    @Test
    void testGetMenusByRestaurant() {
        Long restaurantId = 1L;
        when(menuRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(
                new Menu("Lunch Menu", "Daily lunch specials", LocalDate.now(), null)
        ));

        List<Menu> menus = menuService.getMenusByRestaurant(restaurantId);

        assertNotNull(menus);
        assertEquals(1, menus.size());
        assertEquals("Lunch Menu", menus.get(0).getName());
        verify(menuRepository, times(1)).findByRestaurantId(restaurantId);
    }

    @Test
    void testGetMenuByDate() {
        Long restaurantId = 1L;
        LocalDate date = LocalDate.now();
        Menu menu = new Menu("Dinner Menu", "Evening specials", date, null);
        when(menuRepository.findByRestaurantIdAndDate(restaurantId, date)).thenReturn(List.of(menu));

        Menu result = menuService.getMenuByDate(restaurantId, date);

        assertNotNull(result);
        assertEquals("Dinner Menu", result.getName());
        verify(menuRepository, times(1)).findByRestaurantIdAndDate(restaurantId, date);
    }

    @Test
    void testGetMenuByDateNotFound() {
        Long restaurantId = 1L;
        LocalDate date = LocalDate.now();
        when(menuRepository.findByRestaurantIdAndDate(restaurantId, date)).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuService.getMenuByDate(restaurantId, date);
        });

        assertEquals("Menu not found for the given date", exception.getMessage());
        verify(menuRepository, times(1)).findByRestaurantIdAndDate(restaurantId, date);
    }

    @Test
    void testAddMenu() {
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Menu menu = new Menu("Dinner Menu", "Evening specials", LocalDate.now(), restaurant);
        when(menuRepository.save(menu)).thenReturn(menu);

        Menu savedMenu = menuService.addMenu(menu);

        assertNotNull(savedMenu);
        assertEquals("Dinner Menu", savedMenu.getName());
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void testAddMenuWithInvalidData() {
        Menu invalidMenu = new Menu(null, null, null, null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuService.addMenu(invalidMenu);
        });

        assertEquals("Invalid menu data", exception.getMessage());
        verify(menuRepository, never()).save(any());
    }

    @Test
    void testUpdateMenu() {
        Long menuId = 1L;
        Menu existingMenu = new Menu("Lunch Menu", "Daily specials", LocalDate.now(), null);
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingMenu));

        Menu updatedMenu = new Menu("Updated Menu", "Updated description", LocalDate.now(), null);
        when(menuRepository.save(existingMenu)).thenReturn(updatedMenu);

        Menu result = menuService.updateMenu(menuId, updatedMenu);

        assertNotNull(result);
        assertEquals("Updated Menu", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(existingMenu);
    }

    @Test
    void testUpdateMenuNotFound() {
        Long menuId = 1L;
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuService.updateMenu(menuId, new Menu());
        });

        assertEquals("Menu not found", exception.getMessage());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, never()).save(any());
    }

    @Test
    void testDeleteMenu() {
        Long menuId = 1L;

        menuService.deleteMenu(menuId);

        verify(menuRepository, times(1)).deleteById(menuId);
    }
}