package deti.tqs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldChromeJupiterTest {

    static final Logger log = LoggerFactory.getLogger(HelloWorldChromeJupiterTest.class);

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.firefoxdriver().driverVersion("0.33.0").setup();
    }

    @BeforeEach
    void setup() {
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("/usr/bin/firefox");
        FirefoxProfile profile = new FirefoxProfile();
        options.setProfile(profile);
        driver = new FirefoxDriver(options);
    }

    @Test
    void test() {
        // Exercise
        String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
        driver.get(sutUrl);
        String title = driver.getTitle();
        log.debug("The title of {} is {}", sutUrl, title);

        // Verify
        assertThat(title).isEqualTo("Hands-On Selenium WebDriver with Java");
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

}