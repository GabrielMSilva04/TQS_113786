package deti.tqs.moliceiro_meals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import deti.tqs.moliceiro_meals.model.MenuItem;
import deti.tqs.moliceiro_meals.service.MenuItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
public class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService menuItemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetItemsByMenu() throws Exception {
        Long menuId = 1L;
        MenuItem item1 = new MenuItem("Pasta", "Creamy pasta", new BigDecimal("12.99"), null, null);
        MenuItem item2 = new MenuItem("Salad", "Fresh garden salad", new BigDecimal("8.99"), null, null);

        when(menuItemService.getItemsByMenu(menuId)).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/api/menu-items/{menuId}", menuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pasta"))
                .andExpect(jsonPath("$[1].name").value("Salad"));

        verify(menuItemService, times(1)).getItemsByMenu(menuId);
    }

    @Test
    void testAddMenuItem() throws Exception {
        MenuItem newItem = new MenuItem("Soup", "Hot tomato soup", new BigDecimal("5.99"), null, null);

        when(menuItemService.addMenuItem(any(MenuItem.class))).thenReturn(newItem);

        mockMvc.perform(post("/api/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Soup"))
                .andExpect(jsonPath("$.description").value("Hot tomato soup"))
                .andExpect(jsonPath("$.price").value(5.99));

        verify(menuItemService, times(1)).addMenuItem(any(MenuItem.class));
    }

    @Test
    void testUpdateMenuItem() throws Exception {
        Long itemId = 1L;
        MenuItem updatedItem = new MenuItem("Burger", "Juicy beef burger", new BigDecimal("10.99"), null, null);

        when(menuItemService.updateMenuItem(eq(itemId), any(MenuItem.class))).thenReturn(updatedItem);

        mockMvc.perform(put("/api/menu-items/{itemId}", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Burger"))
                .andExpect(jsonPath("$.description").value("Juicy beef burger"))
                .andExpect(jsonPath("$.price").value(10.99));

        verify(menuItemService, times(1)).updateMenuItem(eq(itemId), any(MenuItem.class));
    }

    @Test
    void testDeleteMenuItem() throws Exception {
        Long itemId = 1L;

        doNothing().when(menuItemService).deleteMenuItem(itemId);

        mockMvc.perform(delete("/api/menu-items/{itemId}", itemId))
                .andExpect(status().isOk());

        verify(menuItemService, times(1)).deleteMenuItem(itemId);
    }
}
