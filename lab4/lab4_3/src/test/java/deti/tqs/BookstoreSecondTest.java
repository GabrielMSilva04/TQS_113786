package deti.tqs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookstoreSecondTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setUpDriver() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new FirefoxDriver();
        driver.get("https://cover-bookstore.onrender.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    void testSearchHarryPotter() {
        // Locate search bar using `data-testid`
        WebElement searchBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='book-search-input']")
        ));
        searchBar.sendKeys("Harry Potter");

        // Locate and click the search button
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class^='Navbar_searchBtn']")
        ));
        searchButton.click();

        // Wait for search results to load
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid='book-search-item']")
        ));

        // Find all book titles in results
        List<WebElement> bookTitles = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("span[class*='SearchList_bookTitle']")
        ));

        // Debugging: Print all found book titles
        bookTitles.forEach(title -> System.out.println("Found Book Title: " + title.getText()));

        // Check if "Harry Potter and the Sorcerer's Stone" is in the results
        boolean bookFound = bookTitles.stream()
                .map(WebElement::getText)
                .anyMatch(title -> title.trim().equalsIgnoreCase("Harry Potter and the Sorcerer's Stone"));

        assertThat(bookFound).isTrue();
    }






    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
