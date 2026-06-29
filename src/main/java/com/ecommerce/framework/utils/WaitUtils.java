package com.ecommerce.framework.utils;

import com.ecommerce.framework.config.ConfigReader;
import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.drivers.DriverFactory;
import com.ecommerce.framework.exceptions.ElementNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils — Centralized synchronization strategies.
 *
 * <p>Responsibility: Eliminate Thread.sleep() usage and provide clean, reusable
 *    wait wrappers that log context-rich information on timeout.
 *
 * <p>All methods operate on the current thread's driver from DriverFactory.
 */
public final class WaitUtils {

    private static final Logger log = LogManager.getLogger(WaitUtils.class);

    private WaitUtils() {}

    // ─────────────────────────────────────────────────────────────
    // Explicit Waits
    // ─────────────────────────────────────────────────────────────

    /**
     * Waits for an element to be visible in the DOM.
     *
     * @param locator the By locator
     * @return the visible WebElement
     */
    public static WebElement waitForVisibility(By locator) {
        return waitForVisibility(locator, getDefaultTimeout());
    }

    /**
     * Waits for an element to be visible with a custom timeout.
     *
     * @param locator  the By locator
     * @param timeoutSeconds custom timeout in seconds
     * @return the visible WebElement
     */
    public static WebElement waitForVisibility(By locator, int timeoutSeconds) {
        log.debug("Waiting up to {}s for element to be visible: {}", timeoutSeconds, locator);
        try {
            return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            throw new ElementNotFoundException(
                    "Element not visible after " + timeoutSeconds + "s | Locator: " + locator, e);
        }
    }

    /**
     * Waits for an element to be clickable.
     *
     * @param locator the By locator
     * @return the clickable WebElement
     */
    public static WebElement waitForClickability(By locator) {
        return waitForClickability(locator, getDefaultTimeout());
    }

    /**
     * Waits for an element to be clickable with a custom timeout.
     */
    public static WebElement waitForClickability(By locator, int timeoutSeconds) {
        log.debug("Waiting up to {}s for element to be clickable: {}", timeoutSeconds, locator);
        try {
            return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            throw new ElementNotFoundException(
                    "Element not clickable after " + timeoutSeconds + "s | Locator: " + locator, e);
        }
    }

    /**
     * Waits for an element to be present in the DOM (not necessarily visible).
     */
    public static WebElement waitForPresence(By locator) {
        log.debug("Waiting for element to be present in DOM: {}", locator);
        try {
            return getWait(getDefaultTimeout()).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            throw new ElementNotFoundException("Element not present in DOM | Locator: " + locator, e);
        }
    }

    /**
     * Waits for the URL to contain a given fragment.
     *
     * @param urlFragment expected URL fragment
     */
    public static void waitForUrlToContain(String urlFragment) {
        log.debug("Waiting for URL to contain: [{}]", urlFragment);
        getWait(getDefaultTimeout()).until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Waits for the page title to contain a given text.
     *
     * @param titleFragment expected title fragment
     */
    public static void waitForTitleToContain(String titleFragment) {
        log.debug("Waiting for title to contain: [{}]", titleFragment);
        getWait(getDefaultTimeout()).until(ExpectedConditions.titleContains(titleFragment));
    }

    /**
     * Waits for an element to become invisible/hidden.
     */
    public static void waitForInvisibility(By locator) {
        log.debug("Waiting for element to be invisible: {}", locator);
        getWait(getDefaultTimeout()).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ─────────────────────────────────────────────────────────────
    // Fluent Wait
    // ─────────────────────────────────────────────────────────────

    /**
     * Fluent wait — polls every 500ms, ignores transient exceptions (NoSuchElement, Stale).
     * Use for highly dynamic/AJAX-heavy elements.
     *
     * @param condition the expected condition to satisfy
     * @param <T>       return type of the condition
     * @return the result of the condition
     */
    public static <T> T fluentWait(ExpectedCondition<T> condition) {
        int timeout  = getFluentTimeout();
        int polling  = getPollingMs();
        log.debug("Fluent wait | Timeout: {}s | Polling: {}ms", timeout, polling);

        FluentWait<WebDriver> fluent = new FluentWait<>(DriverFactory.getDriver())
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofMillis(polling))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Fluent wait timed out after " + timeout + "s");

        return fluent.until(condition);
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────

    private static WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    private static int getDefaultTimeout() {
        return ConfigReader.getInstance().getIntProperty("explicit.wait.timeout");
    }

    private static int getFluentTimeout() {
        return ConfigReader.getInstance().getIntProperty("fluent.wait.timeout");
    }

    private static int getPollingMs() {
        return ConfigReader.getInstance().getIntProperty("fluent.wait.polling");
    }
}
