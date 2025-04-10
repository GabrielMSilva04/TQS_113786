package deti.tqs.moliceiro_meals.controller.api;

import org.springframework.web.bind.annotation.*;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.model.Menu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "Get menus by restaurant ID", description = "Fetch all menus for a specific restaurant")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved menus")
    @GetMapping("/{restaurantId}")
    public List<Menu> getMenusByRestaurant(@PathVariable Long restaurantId) {
        return menuService.getMenusByRestaurant(restaurantId);
    }

    @GetMapping("/{restaurantId}/date")
    public Menu getMenuByDate(@PathVariable Long restaurantId, @RequestParam String date) {
        return menuService.getMenuByDate(restaurantId, LocalDate.parse(date));
    }

    @PostMapping
    public Menu addMenu(@RequestBody Menu menu) {
        if (menu.getName() == null || menu.getDescription() == null || menu.getDate() == null || menu.getRestaurant() == null) {
            throw new RuntimeException("Invalid menu data");
        }
        return menuService.addMenu(menu);
    }

    @PutMapping("/{menuId}")
    public Menu updateMenu(@PathVariable Long menuId, @RequestBody Menu updatedMenu) {
        return menuService.updateMenu(menuId, updatedMenu);
    }

    @DeleteMapping("/{menuId}")
    public void deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
    }
}
