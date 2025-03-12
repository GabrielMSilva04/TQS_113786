package deti.tqs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import deti.tqs.JoinToptalPage;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JoinToptalTest {
    private WebDriver driver;
    private JoinToptalPage joinToptalPage;

    @BeforeAll
    void setUpDriver() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new FirefoxDriver();
        driver.get("https://www.toptal.com/talent/apply");

        joinToptalPage = new JoinToptalPage(driver);
    }

    @Test
    void testApplyAsDeveloper() {
        joinToptalPage.applyAsDeveloper("John Doe", "john.doe@example.com", "SecurePass_123");

        // Validate expected redirection or success message
        Assertions.assertTrue(driver.getCurrentUrl().contains("next-step"),
                "Application did not proceed to the next step.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
