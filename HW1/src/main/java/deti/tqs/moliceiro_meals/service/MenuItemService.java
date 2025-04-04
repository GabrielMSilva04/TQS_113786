package deti.tqs.moliceiro_meals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import deti.tqs.moliceiro_meals.repository.MenuItemRepository;
import deti.tqs.moliceiro_meals.model.MenuItem;

import java.util.List;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getItemsByMenu(Long menuId) {
        return menuItemRepository.findByMenuId(menuId);
    }

    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Long itemId, MenuItem updatedItem) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setPrice(updatedItem.getPrice());
        item.setType(updatedItem.getType());
        return menuItemRepository.save(item);
    }

    public void deleteMenuItem(Long itemId) {
        menuItemRepository.deleteById(itemId);
    }
}
