package deti.tqs.moliceiro_meals.controller.api;

import org.springframework.web.bind.annotation.*;
import deti.tqs.moliceiro_meals.service.MenuItemService;
import deti.tqs.moliceiro_meals.model.MenuItem;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/{menuId}")
    public List<MenuItem> getItemsByMenu(@PathVariable Long menuId) {
        return menuItemService.getItemsByMenu(menuId);
    }

    @PostMapping
    public MenuItem addMenuItem(@RequestBody MenuItem menuItem) {
        return menuItemService.addMenuItem(menuItem);
    }

    @PutMapping("/{itemId}")
    public MenuItem updateMenuItem(@PathVariable Long itemId, @RequestBody MenuItem updatedItem) {
        return menuItemService.updateMenuItem(itemId, updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void deleteMenuItem(@PathVariable Long itemId) {
        menuItemService.deleteMenuItem(itemId);
    }
}
