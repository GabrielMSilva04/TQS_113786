package deti.tqs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookstoreSearchSteps {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://cover-bookstore.onrender.com/");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("I am on the bookstore homepage")
    public void i_am_on_the_bookstore_homepage() {
        driver.get("https://cover-bookstore.onrender.com/");
    }

    @When("I search for {string}")
    public void i_search_for(String query) {
        WebElement searchBar = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='book-search-input']")
        ));
        searchBar.sendKeys(query);

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[class^='Navbar_searchBtn']")
        ));
        searchButton.click();
    }

    @Then("I should see results containing {string}")
    public void i_should_see_results_containing(String expectedTitle) {
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid='book-search-item']")
        ));

        List<WebElement> bookTitles = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("span[class*='SearchList_bookTitle']")
        ));

        boolean bookFound = bookTitles.stream()
                .map(WebElement::getText)
                .anyMatch(title -> title.trim().equalsIgnoreCase(expectedTitle));

        assertThat(bookFound).isTrue();
    }

    @Then("I should see no results")
    public void i_should_see_no_results() {
        List<WebElement> bookTitles = driver.findElements(By.cssSelector("span[class*='SearchList_bookTitle']"));
        assertThat(bookTitles).isEmpty();
    }

    @Then("I should see all available books")
    public void i_should_see_all_available_books() {
        List<WebElement> bookTitles = driver.findElements(By.cssSelector("span[class*='SearchList_bookTitle']"));
        assertThat(bookTitles).isNotEmpty();
    }
}