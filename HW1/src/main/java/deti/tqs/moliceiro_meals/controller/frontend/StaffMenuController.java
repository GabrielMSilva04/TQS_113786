package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.MenuItem;
import deti.tqs.moliceiro_meals.model.MenuItemType;
import deti.tqs.moliceiro_meals.service.MenuItemService;
import deti.tqs.moliceiro_meals.service.MenuService;
import deti.tqs.moliceiro_meals.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/staff/menus")
public class StaffMenuController {

    private static final Logger logger = LoggerFactory.getLogger(StaffMenuController.class);

    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;

    public StaffMenuController(MenuService menuService, MenuItemService menuItemService, RestaurantService restaurantService) {
        this.menuService = menuService;
        this.menuItemService = menuItemService;
        this.restaurantService = restaurantService;
    }
    
    @GetMapping("/{id}")
    public String viewMenu(@PathVariable Long id, Model model) {
        logger.info("Viewing details for menu ID: {}", id);
        
        var menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Menu not found");
            return "redirect:/staff/restaurants";
        }
        
        Menu menu = menuOpt.get();
        model.addAttribute("menu", menu);
        model.addAttribute("restaurant", menu.getRestaurant());
        model.addAttribute("pageTitle", menu.getName() + " - Staff Dashboard");
        
        return "pages/staff/menu-details";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying edit form for menu ID: {}", id);
        
        var menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu not found");
            return "redirect:/staff/restaurants";
        }
        
        model.addAttribute("menu", menuOpt.get());
        model.addAttribute("restaurant", menuOpt.get().getRestaurant());
        model.addAttribute("itemTypes", MenuItemType.values());
        model.addAttribute("pageTitle", "Edit Menu - Staff Dashboard");
        
        return "pages/staff/menu-form";
    }
    
    @PostMapping("/create")
    public String createMenu(@ModelAttribute Menu menu, 
                            BindingResult result, 
                            @RequestParam(required = false) List<String> itemNames,
                            @RequestParam(required = false) List<String> itemDescriptions,
                            @RequestParam(required = false) List<BigDecimal> itemPrices,
                            @RequestParam(required = false) List<MenuItemType> itemTypes,
                            RedirectAttributes redirectAttributes) {
        logger.info("Processing menu creation: {}", menu.getName());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors in menu creation form");
            return "pages/staff/menu-form";
        }
        
        try {
            Menu savedMenu = menuService.addMenu(menu);
            
            // Add menu items if they exist
            if (itemNames != null && !itemNames.isEmpty()) {
                List<MenuItem> menuItems = new ArrayList<>();
                for (int i = 0; i < itemNames.size(); i++) {
                    if (itemNames.get(i) != null && !itemNames.get(i).trim().isEmpty()) {
                        MenuItem item = new MenuItem();
                        item.setName(itemNames.get(i));
                        item.setDescription(i < itemDescriptions.size() ? itemDescriptions.get(i) : "");
                        item.setPrice(i < itemPrices.size() ? itemPrices.get(i) : BigDecimal.ZERO);
                        item.setType(i < itemTypes.size() ? itemTypes.get(i) : MenuItemType.MAIN_COURSE);
                        item.setMenu(savedMenu);
                        menuItems.add(item);
                        menuItemService.addMenuItem(item);
                    }
                }
                savedMenu.setItems(menuItems);
                menuService.updateMenu(savedMenu.getId(), savedMenu);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Menu created successfully");
            return "redirect:/staff/restaurants/" + menu.getRestaurant().getId();
        } catch (Exception e) {
            logger.error("Error creating menu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create menu: " + e.getMessage());
            return "redirect:/staff/restaurants/" + menu.getRestaurant().getId() + "/menus/create";
        }
    }
    
    @PostMapping("/{id}/update")
    public String updateMenu(@PathVariable Long id, 
                           @ModelAttribute Menu menu,
                           BindingResult result,
                           @RequestParam(required = false) List<Long> itemIds,
                           @RequestParam(required = false) List<String> itemNames,
                           @RequestParam(required = false) List<String> itemDescriptions,
                           @RequestParam(required = false) List<BigDecimal> itemPrices,
                           @RequestParam(required = false) List<MenuItemType> itemTypes,
                           RedirectAttributes redirectAttributes) {
        logger.info("Updating menu ID: {}", id);
        
        if (result.hasErrors()) {
            logger.warn("Validation errors in menu update form");
            return "pages/staff/menu-form";
        }
        
        try {
            // Ensure the ID matches the path variable
            menu.setId(id);
            Menu savedMenu = menuService.updateMenu(id, menu);
            
            // Process menu items
            if (itemNames != null && !itemNames.isEmpty()) {
                List<MenuItem> updatedItems = new ArrayList<>();
                
                for (int i = 0; i < itemNames.size(); i++) {
                    if (itemNames.get(i) != null && !itemNames.get(i).trim().isEmpty()) {
                        MenuItem item;
                        
                        // Check if this is an existing item or a new one
                        if (itemIds != null && i < itemIds.size() && itemIds.get(i) != null) {
                            // Update existing item
                            item = new MenuItem();
                            item.setId(itemIds.get(i));
                            item.setMenu(savedMenu);
                        } else {
                            // Create new item
                            item = new MenuItem();
                            item.setMenu(savedMenu);
                        }
                        
                        item.setName(itemNames.get(i));
                        item.setDescription(i < itemDescriptions.size() ? itemDescriptions.get(i) : "");
                        item.setPrice(i < itemPrices.size() ? itemPrices.get(i) : BigDecimal.ZERO);
                        item.setType(i < itemTypes.size() ? itemTypes.get(i) : MenuItemType.MAIN_COURSE);
                        
                        if (item.getId() != null) {
                            menuItemService.updateMenuItem(item.getId(), item);
                        } else {
                            menuItemService.addMenuItem(item);
                        }
                        
                        updatedItems.add(item);
                    }
                }
                
                // Remove items that were deleted in the form
                if (savedMenu.getItems() != null) {
                    List<Long> existingIds = new ArrayList<>();
                    if (itemIds != null) {
                        existingIds.addAll(itemIds);
                    }
                    
                    for (MenuItem existingItem : savedMenu.getItems()) {
                        if (existingItem.getId() != null && !existingIds.contains(existingItem.getId())) {
                            menuItemService.deleteMenuItem(existingItem.getId());
                        }
                    }
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Menu updated successfully");
            return "redirect:/staff/menus/" + id;
        } catch (Exception e) {
            logger.error("Error updating menu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update menu: " + e.getMessage());
            return "redirect:/staff/menus/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteMenu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Deleting menu ID: {}", id);
        
        Optional<Menu> menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu not found");
            return "redirect:/staff/restaurants";
        }
        
        Long restaurantId = menuOpt.get().getRestaurant().getId();
        
        try {
            menuService.deleteMenu(id);
            redirectAttributes.addFlashAttribute("successMessage", "Menu deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting menu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete menu: " + e.getMessage());
        }
        
        return "redirect:/staff/restaurants/" + restaurantId;
    }
    
    @PostMapping("/{menuId}/items/{itemId}/delete")
    public String deleteMenuItem(@PathVariable Long menuId, @PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        logger.info("Deleting menu item ID: {} from menu ID: {}", itemId, menuId);
        
        try {
            menuItemService.deleteMenuItem(itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting menu item: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete menu item: " + e.getMessage());
        }
        
        return "redirect:/staff/menus/" + menuId + "/edit";
    }

    @GetMapping("/{id}/items")
    public String showMenuItemsPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying menu items page for menu ID: {}", id);
        
        var menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu not found");
            return "redirect:/staff/restaurants";
        }
        
        model.addAttribute("menu", menuOpt.get());
        model.addAttribute("restaurant", menuOpt.get().getRestaurant());
        model.addAttribute("pageTitle", "Manage Menu Items - Staff Dashboard");
        
        return "pages/staff/menu-items";
    }

    @PostMapping("/{id}/items/save")
    public String saveMenuItems(@PathVariable Long id, 
                                @RequestParam Map<String, String> formParams,
                                RedirectAttributes redirectAttributes) {
        logger.info("Saving menu items for menu ID: {}", id);
        
        try {
            menuService.updateMenuItems(id, formParams);
            
            redirectAttributes.addFlashAttribute("successMessage", "Menu items saved successfully");
            return "redirect:/staff/menus/" + id;
        } catch (Exception e) {
            logger.error("Error saving menu items", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving menu items: " + e.getMessage());
            return "redirect:/staff/menus/" + id + "/items";
        }
    }

    @PostMapping("/{id}/items/add")
    public String addMenuItem(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam BigDecimal price,
                             @RequestParam MenuItemType type,
                             @RequestParam(required = false) String description,
                             RedirectAttributes redirectAttributes) {
        logger.info("Adding menu item to menu ID: {}", id);
        
        try {
            // Get the menu
            Optional<Menu> menuOpt = menuService.getMenuById(id);
            if (menuOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Menu not found");
                return "redirect:/staff/restaurants";
            }
            
            // Create the menu item
            MenuItem item = new MenuItem();
            item.setName(name);
            item.setPrice(price);
            item.setType(type);
            item.setDescription(description != null ? description : "");
            item.setMenu(menuOpt.get());
            
            // Save the menu item
            menuItemService.addMenuItem(item);
            
            redirectAttributes.addFlashAttribute("successMessage", "Menu item added successfully");
        } catch (Exception e) {
            logger.error("Error adding menu item", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding menu item: " + e.getMessage());
        }
        
        return "redirect:/staff/menus/" + id + "/items";
    }
}