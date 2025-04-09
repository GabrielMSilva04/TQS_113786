package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.repository.MenuRepository;
import deti.tqs.moliceiro_meals.model.Menu;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Cacheable(value = "menus", key = "#restaurantId")
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

    @CacheEvict(value = "menus", allEntries = true)
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
}
