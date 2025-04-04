package deti.tqs.moliceiro_meals.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private MenuItemType type;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    // Constructors
    public MenuItem() {}

    public MenuItem(String name, String description, BigDecimal price, MenuItemType type, Menu menu) {
        this.name = name;
        this.description = description;
        setPrice(price);
        this.type = type;
        this.menu = menu;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public MenuItemType getType() {
        return type;
    }

    public void setType(MenuItemType type) {
        this.type = type;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}