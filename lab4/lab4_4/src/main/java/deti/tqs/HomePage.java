package deti.tqs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Page URL
    private static final String PAGE_URL = "https://www.toptal.com";

    // Locators

    // "Apply as Developer" Button
    @FindBy(linkText = "Apply as a Freelancer")
    private WebElement developerApplyButton;

    // Constructor
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Open URL
        driver.get(PAGE_URL);

        // Initialize Elements
        PageFactory.initElements(driver, this);

        // Wait for the home page to load
        waitForHomePageToLoad();
    }

    private void waitForHomePageToLoad() {
        wait.until(ExpectedConditions.visibilityOf(developerApplyButton));
    }

    public void clickOnDeveloperApplyButton() {
        wait.until(ExpectedConditions.elementToBeClickable(developerApplyButton)).click();
    }
}