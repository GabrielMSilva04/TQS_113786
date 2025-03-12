package deti.tqs;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class JoinToptalPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    @FindBy(xpath = "//h1[contains(text(), 'Apply to Join')]")
    private WebElement pageHeading;

    @FindBy(xpath = "//div[@role='button' and @aria-haspopup='listbox']")
    private WebElement applyingAsDropdown;

    @FindBy(name = "fullName")
    private WebElement fullNameInput;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(name = "passwordConfirmation")
    private WebElement confirmPasswordInput;

    @FindBy(xpath = "//button[@data-testid='apply-form-button']")
    private WebElement applyButton;

    // Constructor
    public JoinToptalPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);

        // Wait for the form to be visible
        waitForPageToLoad();
    }

    public boolean isPageOpened() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(pageHeading)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void waitForPageToLoad() {
        wait.until(ExpectedConditions.visibilityOf(applyButton));
    }

    public void selectDeveloper() {
        System.out.println("Opening the dropdown...");

        // Click to open the dropdown
        wait.until(ExpectedConditions.elementToBeClickable(applyingAsDropdown)).click();

        // Wait for the dropdown list to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul[@role='listbox']")));

        System.out.println("Selecting Developer...");

        // Locate and click the "Developer" option
        WebElement developerOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[@role='listbox']//li//strong[text()='Developer']")
        ));

        // Scroll into view before clicking
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", developerOption);

        // Click the option
        developerOption.click();
    }


    // Fill out the form
    public void enterFullName(String fullName) {
        System.out.println("Entering full name...");
        fullNameInput.sendKeys(fullName);
    }

    public void enterEmail(String email) {
        System.out.println("Entering email...");
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password) {
        System.out.println("Entering password...");
        passwordInput.sendKeys(password);
        confirmPasswordInput.sendKeys(password); // Confirm password
    }

    // Click Apply button
    public void clickApply() {
        applyButton.click();
    }

    // Method to complete the developer signup process
    public void applyAsDeveloper(String fullName, String email, String password) {
        selectDeveloper();
        enterFullName(fullName);
        enterEmail(email);
        enterPassword(password);
        clickApply();
    }
}