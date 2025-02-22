package deti.tqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;

public class ProductFinderServiceIT {
    
    private ProductFinderService productFinderService;

    @BeforeEach
    void setUp() {
        productFinderService = new ProductFinderService(new TqsBasicHttpClient());
    }

    @Test
    void findProductDetailsTest() {
        
        Product found = productFinderService.findProductDetails(1);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1);
        assertThat(found.getTitle()).contains("Fjallraven");
        assertThat(found.getCategory()).isEqualTo("men's clothing");
        assertThat(found.getPrice()).isPositive();
    }

    @Test
    void whenGetProduct_withId3_thenReturnMensCottonJacket() {
        // act
        Product product = productFinderService.findProductDetails(3);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(3);
        assertThat(product.getTitle()).isEqualTo("Mens Cotton Jacket");
        assertThat(product.getPrice()).isEqualTo(55.99);
        assertThat(product.getCategory()).isEqualTo("men's clothing");
        assertThat(product.getImage()).isNotEmpty();
        assertThat(product.getDescription()).isNotEmpty();
    }

    @Test
    void whenGetProduct_withId300_thenReturnEmpty() {
        // act
        Product product = productFinderService.findProductDetails(300);

        assertThat(product).isNull();
    }
} 