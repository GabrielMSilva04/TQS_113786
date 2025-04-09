package deti.tqs.moliceiro_meals.controller.frontend;

import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.MenuItem;
import deti.tqs.moliceiro_meals.model.MenuItemType;
import deti.tqs.moliceiro_meals.service.MenuItemService;
import deti.tqs.moliceiro_meals.service.MenuService;
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
    
    // View template constants
    private static final String VIEW_MENU_DETAILS = "pages/staff/menu-details";
    private static final String VIEW_MENU_FORM = "pages/staff/menu-form";
    private static final String VIEW_MENU_ITEMS = "pages/staff/menu-items";
    
    // Model attribute constants
    private static final String ATTR_MENU = "menu";
    private static final String ATTR_RESTAURANT = "restaurant";
    private static final String ATTR_PAGE_TITLE = "pageTitle";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_ITEM_TYPES = "itemTypes";
    
    // Redirect URL constants
    private static final String REDIRECT_STAFF_RESTAURANTS = "redirect:/staff/restaurants";
    private static final String REDIRECT_STAFF_MENUS = "redirect:/staff/menus/";
    private static final String REDIRECT_STAFF_RESTAURANTS_ID = "redirect:/staff/restaurants/";
    
    // Error message constants
    private static final String ERROR_MENU_NOT_FOUND = "Menu not found";
    private static final String ERROR_MENU_UPDATE = "Failed to update menu: ";
    private static final String ERROR_MENU_DELETE = "Failed to delete menu: ";
    private static final String ERROR_MENU_ITEM_DELETE = "Failed to delete menu item: ";
    private static final String ERROR_MENU_ITEMS_SAVE = "Error saving menu items: ";
    private static final String ERROR_MENU_ITEM_ADD = "Error adding menu item: ";
    
    // Success message constants
    private static final String SUCCESS_MENU_CREATED = "Menu created successfully";
    private static final String SUCCESS_MENU_UPDATED = "Menu updated successfully";
    private static final String SUCCESS_MENU_DELETED = "Menu deleted successfully";
    private static final String SUCCESS_MENU_ITEM_DELETED = "Menu item deleted successfully";
    private static final String SUCCESS_MENU_ITEMS_SAVED = "Menu items saved successfully";
    private static final String SUCCESS_MENU_ITEM_ADDED = "Menu item added successfully";

    private final MenuService menuService;
    private final MenuItemService menuItemService;

    public StaffMenuController(MenuService menuService, MenuItemService menuItemService) {
        this.menuService = menuService;
        this.menuItemService = menuItemService;
    }
    
    @GetMapping("/{id}")
    public String viewMenu(@PathVariable Long id, Model model) {
        logger.info("Viewing details for menu ID: {}", id);
        
        var menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            model.addAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        Menu menu = menuOpt.get();
        model.addAttribute(ATTR_MENU, menu);
        model.addAttribute(ATTR_RESTAURANT, menu.getRestaurant());
        model.addAttribute(ATTR_PAGE_TITLE, menu.getName() + " - Staff Dashboard");
        
        return VIEW_MENU_DETAILS;
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying edit form for menu ID: {}", id);
        
        var menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        model.addAttribute(ATTR_MENU, menuOpt.get());
        model.addAttribute(ATTR_RESTAURANT, menuOpt.get().getRestaurant());
        model.addAttribute(ATTR_ITEM_TYPES, MenuItemType.values());
        model.addAttribute(ATTR_PAGE_TITLE, "Edit Menu - Staff Dashboard");
        
        return VIEW_MENU_FORM;
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
            return VIEW_MENU_FORM;
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
            
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_CREATED);
            return REDIRECT_STAFF_RESTAURANTS_ID + menu.getRestaurant().getId();
        } catch (Exception e) {
            logger.error("Error creating menu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "Failed to create menu: " + e.getMessage());
            return REDIRECT_STAFF_RESTAURANTS_ID + menu.getRestaurant().getId() + "/menus/create";
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
            return VIEW_MENU_FORM;
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
            
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_UPDATED);
            return REDIRECT_STAFF_MENUS + id;
        } catch (Exception e) {
            logger.error("Error updating menu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_UPDATE + e.getMessage());
            return REDIRECT_STAFF_MENUS + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteMenu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Deleting menu ID: {}", id);
        
        Optional<Menu> menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        Long restaurantId = menuOpt.get().getRestaurant().getId();
        
        try {
            menuService.deleteMenu(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_DELETED);
        } catch (Exception e) {
            logger.error("Error deleting menu: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_DELETE + e.getMessage());
        }
        
        return REDIRECT_STAFF_RESTAURANTS_ID + restaurantId;
    }
    
    @PostMapping("/{menuId}/items/{itemId}/delete")
    public String deleteMenuItem(@PathVariable Long menuId, @PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        logger.info("Deleting menu item ID: {} from menu ID: {}", itemId, menuId);
        
        try {
            menuItemService.deleteMenuItem(itemId);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_ITEM_DELETED);
        } catch (Exception e) {
            logger.error("Error deleting menu item: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_ITEM_DELETE + e.getMessage());
        }
        
        return REDIRECT_STAFF_MENUS + menuId + "/edit";
    }

    @GetMapping("/{id}/items")
    public String showMenuItemsPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Displaying menu items page for menu ID: {}", id);
        
        var menuOpt = menuService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_FOUND);
            return REDIRECT_STAFF_RESTAURANTS;
        }
        
        model.addAttribute(ATTR_MENU, menuOpt.get());
        model.addAttribute(ATTR_RESTAURANT, menuOpt.get().getRestaurant());
        model.addAttribute(ATTR_PAGE_TITLE, "Manage Menu Items - Staff Dashboard");
        
        return VIEW_MENU_ITEMS;
    }

    @PostMapping("/{id}/items/save")
    public String saveMenuItems(@PathVariable Long id, 
                                @RequestParam Map<String, String> formParams,
                                RedirectAttributes redirectAttributes) {
        logger.info("Saving menu items for menu ID: {}", id);
        
        try {
            menuService.updateMenuItems(id, formParams);
            
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_ITEMS_SAVED);
            return REDIRECT_STAFF_MENUS + id;
        } catch (Exception e) {
            logger.error("Error saving menu items", e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_ITEMS_SAVE + e.getMessage());
            return REDIRECT_STAFF_MENUS + id + "/items";
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
                redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_NOT_FOUND);
                return REDIRECT_STAFF_RESTAURANTS;
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
            
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, SUCCESS_MENU_ITEM_ADDED);
        } catch (Exception e) {
            logger.error("Error adding menu item", e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ERROR_MENU_ITEM_ADD + e.getMessage());
        }
        
        return REDIRECT_STAFF_MENUS + id + "/items";
    }
}