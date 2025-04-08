package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.repository.MenuRepository;
import deti.tqs.moliceiro_meals.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Cacheable(value = "menus", key = "#restaurantId")
    public List<Menu> getMenusByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    public Menu getMenuByDate(Long restaurantId, LocalDate date) {
        return menuRepository.findByRestaurantIdAndDate(restaurantId, date)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Menu not found for the given date"));
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

    @CacheEvict(value = "menus", allEntries = true)
    public void deleteMenu(Long menuId) {
        menuRepository.deleteById(menuId);
    }
}
