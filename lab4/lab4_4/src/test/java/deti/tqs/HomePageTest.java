package deti.tqs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import deti.tqs.HomePage;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HomePageTest {
    private WebDriver driver;
    private HomePage homePage;

    @BeforeAll
    void setUpDriver() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new FirefoxDriver();
        homePage = new HomePage(driver);
    }

    @Test
    void testClickApplyAsDeveloper() {
        homePage.clickOnDeveloperApplyButton();

        // Verify that the Apply page opened
        Assertions.assertTrue(driver.getCurrentUrl().contains("apply"),
                "Failed to navigate to Apply as Developer page.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}