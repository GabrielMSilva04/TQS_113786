package deti.tqs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;

import java.util.Map;

public class Selecttrip1Test {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;

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

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void selecttrip1() {
        driver.get("https://blazedemo.com/");
        driver.manage().window().setSize(new Dimension(1074, 1090));
        {
            WebElement dropdown = driver.findElement(By.name("fromPort"));
            dropdown.findElement(By.xpath("//option[. = 'San Diego']")).click();
        }
        {
            WebElement element = driver.findElement(By.name("fromPort"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).clickAndHold().perform();
        }
        {
            WebElement element = driver.findElement(By.name("fromPort"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        {
            WebElement element = driver.findElement(By.name("fromPort"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).release().perform();
        }
        {
            WebElement dropdown = driver.findElement(By.name("toPort"));
            dropdown.findElement(By.xpath("//option[. = 'New York']")).click();
        }
        {
            WebElement element = driver.findElement(By.name("toPort"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).clickAndHold().perform();
        }
        {
            WebElement element = driver.findElement(By.name("toPort"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        {
            WebElement element = driver.findElement(By.name("toPort"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).release().perform();
        }
        driver.findElement(By.cssSelector(".btn-primary")).click();
        driver.findElement(By.cssSelector("tr:nth-child(1) .btn")).click();
        driver.findElement(By.id("inputName")).click();
        driver.findElement(By.id("inputName")).sendKeys("Gabriel Silva");
        driver.findElement(By.cssSelector(".checkbox")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertThat(driver.getTitle(), is("BlazeDemo Confirmation"));
    }

}