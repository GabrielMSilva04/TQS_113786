package deti.tqs.moliceiro_meals.unit.model;

import deti.tqs.moliceiro_meals.model.MenuItemType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTypeTest {

    @Test
    void testEnumValues() {
        assertEquals("APPETIZER", MenuItemType.APPETIZER.name());
        assertEquals("MAIN_COURSE", MenuItemType.MAIN_COURSE.name());
        assertEquals("DESSERT", MenuItemType.DESSERT.name());
        assertEquals("BEVERAGE", MenuItemType.BEVERAGE.name());
    }
}