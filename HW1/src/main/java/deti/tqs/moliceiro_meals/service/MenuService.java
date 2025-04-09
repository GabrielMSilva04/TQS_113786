package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.repository.MenuRepository;
import deti.tqs.moliceiro_meals.model.Menu;
import deti.tqs.moliceiro_meals.model.MenuItem;
import deti.tqs.moliceiro_meals.repository.MenuItemRepository;
import deti.tqs.moliceiro_meals.model.MenuItemType;
import deti.tqs.moliceiro_meals.model.Restaurant;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemService menuItemService;

    public MenuService(MenuRepository menuRepository, MenuItemRepository menuItemRepository, MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<Menu> getMenusByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    public Menu getMenuByDate(Long restaurantId, LocalDate date) {
        List<Menu> menus = menuRepository.findByRestaurantIdAndDate(restaurantId, date);
        if (menus.isEmpty()) {
            throw new RuntimeException("Menu not found for the given date");
        }
        return menus.get(0);
    }

    public List<Menu> getMenusForUpcomingDays(Long restaurantId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return menuRepository.findByRestaurantIdAndDateBetween(restaurantId, today, endDate);
    }

    public Menu addMenu(Menu menu) {
        if (menu.getName() == null || menu.getDescription() == null || menu.getDate() == null || menu.getRestaurant() == null) {
            throw new RuntimeException("Invalid menu data");
        }
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Long menuId, Menu updatedMenu) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        menu.setName(updatedMenu.getName());
        menu.setDescription(updatedMenu.getDescription());
        menu.setDate(updatedMenu.getDate());
        return menuRepository.save(menu);
    }

    public void deleteMenu(Long menuId) {
        menuRepository.deleteById(menuId);
    }

    public Optional<Menu> getMenuById(Long menuId) {
        return menuRepository.findById(menuId);
    }

    public List<Menu> getMenusByRestaurantAndDate(Long id, LocalDate today) {
        try {
            List<Menu> menus = menuRepository.findByRestaurantIdAndDate(id, today);
            if (menus == null || menus.isEmpty()) {
                return new ArrayList<>();
            }
            return menus;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public MenuItem addMenuItem(MenuItem menuItem) {
        if (menuItem.getName() == null || menuItem.getMenu() == null) {
            throw new RuntimeException("Invalid menu item data");
        }
        return menuItemRepository.save(menuItem);
    }

    public Optional<MenuItem> getMenuItemById(Long itemId) {
        return menuItemRepository.findById(itemId);
    }

    public void deleteMenuItem(Long itemId) {
        menuItemRepository.deleteById(itemId);
    }

    public Menu updateMenuItems(Long menuId, Map<String, String> formParams) {
        
        Optional<Menu> menuOpt = getMenuById(menuId);
        if (menuOpt.isEmpty()) {
            throw new RuntimeException("Menu not found with ID: " + menuId);
        }
        
        Menu menu = menuOpt.get();
        List<MenuItem> currentItems = menu.getItems() != null ? new ArrayList<>(menu.getItems()) : new ArrayList<>();
        List<MenuItem> updatedItems = new ArrayList<>();
        Set<Long> processedItemIds = new HashSet<>();
        
        int maxIndex = -1;
        
        // Find the highest index in the form data
        for (String key : formParams.keySet()) {
            if (key.startsWith("items[") && key.contains("].")) {
                try {
                    int startIndex = key.indexOf('[') + 1;
                    int endIndex = key.indexOf(']');
                    int index = Integer.parseInt(key.substring(startIndex, endIndex));
                    maxIndex = Math.max(maxIndex, index);
                } catch (Exception e) {
                }
            }
        }
        
        for (int i = 0; i <= maxIndex; i++) {
            String nameKey = "items[" + i + "].name";
            String priceKey = "items[" + i + "].price";
            String typeKey = "items[" + i + "].type";
            String descriptionKey = "items[" + i + "].description";
            String idKey = "items[" + i + "].id";
            
            String name = formParams.get(nameKey);
            String priceStr = formParams.get(priceKey);
            String typeStr = formParams.get(typeKey);
            String description = formParams.get(descriptionKey);
            String idStr = formParams.get(idKey);
            
            if (name == null || name.trim().isEmpty() || priceStr == null || priceStr.trim().isEmpty()) {
                continue;
            }
            
            try {
                MenuItem item;
                boolean isNewItem = (idStr == null || idStr.trim().isEmpty());
                
                if (!isNewItem) {
                    // Existing item - find it or create new if it doesn't exist
                    Long itemId = Long.parseLong(idStr);
                    processedItemIds.add(itemId);
                    
                    Optional<MenuItem> existingItem = menuItemService.getMenuItemById(itemId);
                    if (existingItem.isPresent()) {
                        item = existingItem.get();
                    } else {
                        item = new MenuItem();
                        item.setMenu(menu);
                    }
                } else {
                    // New item
                    item = new MenuItem();
                    item.setMenu(menu);
                }
                
                // Update item fields
                item.setName(name);
                
                try {
                    BigDecimal price = new BigDecimal(priceStr.replace(',', '.'));
                    item.setPrice(price);
                } catch (NumberFormatException e) {
                    item.setPrice(BigDecimal.ZERO);
                }
                
                // Parse type
                if (typeStr != null && !typeStr.trim().isEmpty()) {
                    try {
                        MenuItemType type = MenuItemType.valueOf(typeStr);
                        item.setType(type);
                    } catch (IllegalArgumentException e) {
                        item.setType(MenuItemType.MAIN_COURSE); // Default type
                    }
                } else {
                    item.setType(MenuItemType.MAIN_COURSE); // Default type
                }
                
                item.setDescription(description != null ? description : "");
                
                // Save the item
                if (isNewItem) {
                    MenuItem savedItem = menuItemService.addMenuItem(item);
                    updatedItems.add(savedItem);
                } else {
                    MenuItem savedItem = menuItemService.updateMenuItem(item.getId(), item);
                    updatedItems.add(savedItem);
                }
                
            } catch (Exception e) {
            }
        }
        
        // Delete items that were removed from the form
        for (MenuItem existingItem : currentItems) {
            if (existingItem.getId() != null && !processedItemIds.contains(existingItem.getId())) {
                menuItemService.deleteMenuItem(existingItem.getId());
            }
        }
        
        // Update the menu with new items
        menu.setItems(updatedItems);
        Menu updatedMenu = updateMenu(menuId, menu);
        
        return updatedMenu;
    }

    public Map<Restaurant, List<Menu>> getTodaysMenusByRestaurant() {
        List<Menu> menus = menuRepository.findByDate(LocalDate.now());
        Map<Restaurant, List<Menu>> restaurantMenus = new HashMap<>();

        for (Menu menu : menus) {
            Restaurant restaurant = menu.getRestaurant();
            restaurantMenus.computeIfAbsent(restaurant, k -> new ArrayList<>()).add(menu);
        }

        return restaurantMenus;
    }
}
