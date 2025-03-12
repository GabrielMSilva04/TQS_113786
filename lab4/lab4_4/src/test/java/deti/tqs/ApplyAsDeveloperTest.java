package deti.tqs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import deti.tqs.HomePage;
import deti.tqs.JoinToptalPage;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplyAsDeveloperTest {
    private WebDriver driver;
    private HomePage homePage;
    private JoinToptalPage joinToptalPage;

    @BeforeAll
    void setUpDriver() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new FirefoxDriver();
    }

    @Test
    void testCompleteApplyProcess() {
        // Step 1: Open HomePage and click "APPLY AS A DEVELOPER"
        homePage = new HomePage(driver);
        homePage.clickOnDeveloperApplyButton();

        // Step 2: Now we are on the Join Toptal Page
        joinToptalPage = new JoinToptalPage(driver);
        assertThat(joinToptalPage.isPageOpened()).isTrue();

        // Step 3: Fill the form and submit
        joinToptalPage.applyAsDeveloper("John Doe", "john.doe@example.com", "SecurePass_123");

        // Step 4: Verify the redirection after submitting
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