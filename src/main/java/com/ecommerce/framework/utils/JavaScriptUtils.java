package com.ecommerce.framework.utils;

import com.ecommerce.framework.drivers.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

/**
 * JavaScriptUtils — Wraps JavaScript execution for advanced interactions.
 *
 * <p>Use when standard Selenium interactions are insufficient, e.g.:
 * <ul>
 *   <li>Elements blocked by overlays (CSS pointer-events: none)</li>
 *   <li>Scrolling to offscreen elements</li>
 *   <li>Dynamically reading page state</li>
 *   <li>Highlighting elements for debugging</li>
 * </ul>
 *
 * <p>Note: JavaScript clicks bypass natural browser event flow (hover states,
 *    CSS transitions). Use only as a fallback, not as a primary interaction strategy.
 */
public final class JavaScriptUtils {

    private static final Logger log = LogManager.getLogger(JavaScriptUtils.class);

    private JavaScriptUtils() {}

    /**
     * Clicks an element using JavaScript.
     * Use as fallback when WebElement.click() throws ElementClickInterceptedException.
     *
     * @param element the target WebElement
     */
    public static void click(WebElement element) {
        log.debug("JavaScript click on element: {}", element);
        executeScript("arguments[0].click();", element);
    }

    /**
     * Types text into an element using JavaScript (bypasses native keyboard events).
     *
     * @param element the target input element
     * @param text    the text to set
     */
    public static void type(WebElement element, String text) {
        log.debug("JavaScript type '{}' into element: {}", text, element);
        executeScript("arguments[0].value = arguments[1];", element, text);
    }

    /**
     * Scrolls the page until the element is in the viewport.
     *
     * @param element the target WebElement
     */
    public static void scrollIntoView(WebElement element) {
        log.debug("Scrolling element into view: {}", element);
        executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    /**
     * Scrolls the page to the very top.
     */
    public static void scrollToTop() {
        log.debug("Scrolling to top of page");
        executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scrolls the page to the very bottom.
     */
    public static void scrollToBottom() {
        log.debug("Scrolling to bottom of page");
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Highlights an element with a red border for debugging purposes.
     *
     * @param element the element to highlight
     */
    public static void highlight(WebElement element) {
        executeScript("arguments[0].style.border='3px solid red';", element);
    }

    /**
     * Returns the current page title via JavaScript.
     *
     * @return page title string
     */
    public static String getPageTitle() {
        return (String) executeScript("return document.title;");
    }

    /**
     * Returns the current page URL via JavaScript.
     *
     * @return current URL string
     */
    public static String getCurrentUrl() {
        return (String) executeScript("return window.location.href;");
    }

    /**
     * Checks if the document is fully loaded (readyState === 'complete').
     *
     * @return true if page is loaded
     */
    public static boolean isPageLoaded() {
        Object state = executeScript("return document.readyState;");
        return "complete".equals(state);
    }

    /**
     * Opens a new browser tab with the given URL.
     *
     * @param url the URL to open in the new tab
     */
    public static void openNewTab(String url) {
        log.info("Opening new tab with URL: {}", url);
        executeScript("window.open('" + url + "', '_blank');");
    }

    // ─────────────────────────────────────────────────────────────
    // Core Executor
    // ─────────────────────────────────────────────────────────────

    /**
     * Executes a JavaScript script with optional arguments.
     *
     * @param script JavaScript to execute
     * @param args   optional arguments passed to the script
     * @return script return value (may be null)
     */
    public static Object executeScript(String script, Object... args) {
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        return js.executeScript(script, args);
    }
}
