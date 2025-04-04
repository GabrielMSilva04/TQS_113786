package deti.tqs.moliceiro_meals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import deti.tqs.moliceiro_meals.controller.api.MenuController;
import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.Restaurant;
import deti.tqs.moliceiro_meals.service.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetMenusByRestaurant() throws Exception {
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Menu menu1 = new Menu("Lunch Menu", "Daily specials", LocalDate.now(), restaurant);
        Menu menu2 = new Menu("Dinner Menu", "Evening specials", LocalDate.now(), restaurant);

        when(menuService.getMenusByRestaurant(restaurantId)).thenReturn(List.of(menu1, menu2));

        mockMvc.perform(get("/api/menus/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Lunch Menu"))
                .andExpect(jsonPath("$[1].name").value("Dinner Menu"));

        verify(menuService, times(1)).getMenusByRestaurant(restaurantId);
    }

    @Test
    void testAddMenu() throws Exception {
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Menu newMenu = new Menu("Brunch Menu", "Weekend specials", LocalDate.now(), restaurant);

        when(menuService.addMenu(any(Menu.class))).thenReturn(newMenu);

        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMenu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Brunch Menu"))
                .andExpect(jsonPath("$.description").value("Weekend specials"));

        verify(menuService, times(1)).addMenu(any(Menu.class));
    }

    @Test
    void testUpdateMenu() throws Exception {
        Long menuId = 1L;
        Restaurant restaurant = new Restaurant("Moliceiro Meals", "Aveiro", "A cozy restaurant by the canals", "123-456-789");
        Menu updatedMenu = new Menu("Updated Menu", "Updated specials", LocalDate.now(), restaurant);

        when(menuService.updateMenu(eq(menuId), any(Menu.class))).thenReturn(updatedMenu);

        mockMvc.perform(put("/api/menus/{menuId}", menuId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMenu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Menu"))
                .andExpect(jsonPath("$.description").value("Updated specials"));

        verify(menuService, times(1)).updateMenu(eq(menuId), any(Menu.class));
    }

    @Test
    void testDeleteMenu() throws Exception {
        Long menuId = 1L;

        doNothing().when(menuService).deleteMenu(menuId);

        mockMvc.perform(delete("/api/menus/{menuId}", menuId))
                .andExpect(status().isOk());

        verify(menuService, times(1)).deleteMenu(menuId);
    }
}
