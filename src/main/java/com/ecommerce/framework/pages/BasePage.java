package com.ecommerce.framework.pages;

import com.ecommerce.framework.drivers.DriverFactory;
import com.ecommerce.framework.reports.ExtentTestManager;
import com.ecommerce.framework.utils.JavaScriptUtils;
import com.ecommerce.framework.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * BasePage — Abstract base class for all Page Object classes.
 *
 * <p>Design Pattern: Template Method — provides common UI interaction primitives
 *    that all page objects inherit without duplicating synchronization logic.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Wraps raw Selenium actions with logging and wait synchronization</li>
 *   <li>Provides consistent element interaction patterns (click, type, select)</li>
 *   <li>All Page Objects extend this class, not WebDriver directly</li>
 * </ul>
 *
 * <p>Key Design Decision: Uses By locators (not PageFactory @FindBy) to avoid
 *    StaleElementReferenceException on AJAX-heavy pages.
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final WebDriver driver;

    /**
     * Constructor — retrieves the current thread's WebDriver from DriverFactory.
     * Page objects must not hold references across tests; they are created fresh each time.
     */
    protected BasePage() {
        this.driver = DriverFactory.getDriver();
    }

    // ─────────────────────────────────────────────────────────────
    // Click Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Waits for element to be clickable, then clicks it.
     *
     * @param locator the By locator
     * @param description human-readable description for logging
     */
    protected void click(By locator, String description) {
        log.debug("Clicking: [{}]", description);
        WaitUtils.waitForClickability(locator).click();
        log(description + " — clicked");
    }

    /**
     * JavaScript click fallback — use only when standard click is intercepted.
     *
     * @param locator     the By locator
     * @param description description for logging
     */
    protected void jsClick(By locator, String description) {
        log.debug("JS Click: [{}]", description);
        WebElement element = WaitUtils.waitForPresence(locator);
        JavaScriptUtils.click(element);
        log(description + " — JS clicked");
    }

    // ─────────────────────────────────────────────────────────────
    // Type/Input Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Waits for element to be visible, clears it, then types the given text.
     *
     * @param locator     the By locator
     * @param text        text to enter
     * @param description description for logging
     */
    protected void type(By locator, String text, String description) {
        log.debug("Typing '{}' into: [{}]", text, description);
        WebElement element = WaitUtils.waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        log(description + " — entered: " + text);
    }

    /**
     * Clears the input field identified by the locator.
     */
    protected void clear(By locator) {
        WaitUtils.waitForVisibility(locator).clear();
    }

    // ─────────────────────────────────────────────────────────────
    // Read Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the trimmed visible text of an element.
     *
     * @param locator     the By locator
     * @param description description for logging
     * @return element's visible text
     */
    protected String getText(By locator, String description) {
        String text = WaitUtils.waitForVisibility(locator).getText().trim();
        log.debug("Got text '{}' from: [{}]", text, description);
        return text;
    }

    /**
     * Returns the value of the given attribute on an element.
     */
    protected String getAttribute(By locator, String attributeName) {
        return WaitUtils.waitForPresence(locator).getAttribute(attributeName);
    }

    /**
     * Checks if an element is currently displayed in the DOM.
     *
     * @param locator the By locator
     * @return true if element is visible, false otherwise
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns all elements matching the locator.
     */
    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    // ─────────────────────────────────────────────────────────────
    // Dropdown
    // ─────────────────────────────────────────────────────────────

    /**
     * Selects a dropdown option by visible text.
     *
     * @param locator     the select element locator
     * @param visibleText the option text to select
     * @param description description for logging
     */
    protected void selectByVisibleText(By locator, String visibleText, String description) {
        log.debug("Selecting '{}' from: [{}]", visibleText, description);
        Select select = new Select(WaitUtils.waitForVisibility(locator));
        select.selectByVisibleText(visibleText);
        log(description + " — selected: " + visibleText);
    }

    /**
     * Selects a dropdown option by index.
     */
    protected void selectByIndex(By locator, int index, String description) {
        log.debug("Selecting index {} from: [{}]", index, description);
        Select select = new Select(WaitUtils.waitForVisibility(locator));
        select.selectByIndex(index);
        log(description + " — selected index: " + index);
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────

    /** Returns the current page URL. */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Returns the current page title. */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /** Navigates the browser to a given URL. */
    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    // ─────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Hovers over an element using the Actions class.
     *
     * @param locator     the By locator
     * @param description description for logging
     */
    protected void hoverOver(By locator, String description) {
        log.debug("Hovering over: [{}]", description);
        WebElement element = WaitUtils.waitForVisibility(locator);
        new Actions(driver).moveToElement(element).perform();
    }

    // ─────────────────────────────────────────────────────────────
    // Reporting Helper
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs a step to Extent Reports and to the logger simultaneously.
     *
     * @param message the step description
     */
    protected void log(String message) {
        log.info(message);
        ExtentTestManager.logInfo(message);
    }
}
