package deti.tqs.moliceiro_meals.service;

import deti.tqs.moliceiro_meals.repository.MenuRepository;
import deti.tqs.moliceiro_meals.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Menu> getMenusByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    public Menu getMenuByDate(Long restaurantId, LocalDate date) {
        return menuRepository.findByRestaurantIdAndDate(restaurantId, date)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Menu not found for the given date"));
    }

    public Menu addMenu(Menu menu) {
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
}
