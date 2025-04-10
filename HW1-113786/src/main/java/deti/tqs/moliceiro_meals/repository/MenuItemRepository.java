package deti.tqs.moliceiro_meals.repository;

import deti.tqs.moliceiro_meals.model.MenuItem;
import deti.tqs.moliceiro_meals.model.MenuItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByMenuId(Long menuId);
    List<MenuItem> findByMenuIdAndType(Long menuId, MenuItemType type);
}