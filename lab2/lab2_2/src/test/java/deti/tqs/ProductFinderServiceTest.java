package deti.tqs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;

@ExtendWith(MockitoExtension.class)
public class ProductFinderServiceTest {
    
    @Mock
    private ISimpleHttpClient httpClient;

    @InjectMocks
    private ProductFinderService productFinderService;

    @Test
    void findProductDetailsTest() {
        Product expectedProduct = new Product(1, "Test Image", 9.99, "Test Title", "Test Category", "Test Description");
        when(httpClient.doHttpGet("https://fakestoreapi.com/products/1")).thenReturn(expectedProduct.toString());
        Product found = productFinderService.findProductDetails(1);

        assertEquals(expectedProduct, found);
    }


    @Test
    void whenGetProduct_withId3_thenReturnMensCottonJacket() {
        // arrange
        String jsonResponse = """
            {
                "id": 3,
                "title": "Mens Cotton Jacket",
                "price": 55.99,
                "category": "men's clothing",
                "description": "great outerwear jackets for Spring/Autumn/Winter",
                "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg",
                "rating": {
                    "rate": 4.7,
                    "count": 500
                }
            }""";
        
        when(httpClient.doHttpGet("https://fakestoreapi.com/products/3")).thenReturn(jsonResponse);

        // act
        Product product = productFinderService.findProductDetails(3);

        // assert
        assertThat(product.getId()).isEqualTo(3);
        assertThat(product.getTitle()).isEqualTo("Mens Cotton Jacket");
    }

    @Test
    void whenGetProduct_withId300_thenReturnEmpty() {
        // arrange
        when(httpClient.doHttpGet("https://fakestoreapi.com/products/300")).thenReturn("");
        
        // act
        Product product = productFinderService.findProductDetails(300);

        // assert
        assertThat(product).isNull();
    }
}
