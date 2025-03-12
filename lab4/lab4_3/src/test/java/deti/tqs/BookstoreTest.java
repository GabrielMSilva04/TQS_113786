package deti.tqs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookstoreTest {
    private WebDriver driver;

    @BeforeAll
    void setUpDriver() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new FirefoxDriver();
        driver.get("https://cover-bookstore.onrender.com/");
    }

    @Test
    void testSearchHarryPotter() {
        // Locate search bar using `data-testid`
        WebElement searchBar = driver.findElement(By.xpath("//input[@data-testid='book-search-input']"));
        searchBar.sendKeys("Harry Potter");

        // Locate and click the search button
        WebElement searchButton = driver.findElement(By.xpath("//button[contains(@class, 'Navbar_searchBtn')]"));
        searchButton.click();

        // Wait for results to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-testid='book-search-item']")));

        // Find all search results
        List<WebElement> books = driver.findElements(By.xpath("//span[contains(@class, 'SearchList_bookTitle')]"));

        // Verify at least one result contains "Harry Potter and the Sorcerer's Stone"
        boolean bookFound = books.stream()
                .anyMatch(book -> book.getText().trim().equals("Harry Potter and the Sorcerer's Stone"));

        assertThat(bookFound).isTrue();
    }



    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}