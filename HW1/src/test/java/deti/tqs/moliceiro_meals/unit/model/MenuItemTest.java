package deti.tqs.moliceiro_meals.unit.model;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.MenuItem;
import deti.tqs.moliceiro_meals.model.MenuItemType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void testConstructorAndGetters() {
        Menu menu = new Menu();
        MenuItem menuItem = new MenuItem("Grilled Fish", "Delicious grilled fish", new BigDecimal("12.99"), MenuItemType.MAIN_COURSE, menu);

        assertEquals("Grilled Fish", menuItem.getName());
        assertEquals("Delicious grilled fish", menuItem.getDescription());
        assertEquals(new BigDecimal("12.99"), menuItem.getPrice());
        assertEquals(MenuItemType.MAIN_COURSE, menuItem.getType());
        assertEquals(menu, menuItem.getMenu());
    }

    @Test
    void testSetters() {
        Menu menu = new Menu();
        MenuItem menuItem = new MenuItem();

        menuItem.setName("Pasta");
        menuItem.setDescription("Creamy pasta");
        menuItem.setPrice(new BigDecimal("8.99"));
        menuItem.setType(MenuItemType.MAIN_COURSE);
        menuItem.setMenu(menu);

        assertEquals("Pasta", menuItem.getName());
        assertEquals("Creamy pasta", menuItem.getDescription());
        assertEquals(new BigDecimal("8.99"), menuItem.getPrice());
        assertEquals(MenuItemType.MAIN_COURSE, menuItem.getType());
        assertEquals(menu, menuItem.getMenu());
    }

    @Test
    void testMenuItemNegativePrice() {
        MenuItem menuItem = new MenuItem();
        assertThrows(IllegalArgumentException.class, () -> menuItem.setPrice(new BigDecimal("-5.99")));
    }
}