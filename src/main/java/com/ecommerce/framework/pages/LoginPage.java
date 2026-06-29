package com.ecommerce.framework.pages;

import com.ecommerce.framework.utils.WaitUtils;
import org.openqa.selenium.By;

/**
 * LoginPage — Page Object for the SauceDemo login page.
 * URL: https://www.saucedemo.com
 *
 * <p>Encapsulates all interactions with the login form.
 * <p>Returns InventoryPage on successful login (fluent page chain pattern).
 */
public class LoginPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators (private — test code must NEVER reference raw locators)
    // ─────────────────────────────────────────────────────────────
    private static final By USERNAME_INPUT  = By.id("user-name");
    private static final By PASSWORD_INPUT  = By.id("password");
    private static final By LOGIN_BUTTON    = By.id("login-button");
    private static final By ERROR_MESSAGE   = By.cssSelector("[data-test='error']");
    private static final By LOGIN_LOGO      = By.className("login_logo");

    // ─────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Navigates to the SauceDemo login page.
     *
     * @param url base URL from configuration
     * @return this LoginPage (for method chaining)
     */
    public LoginPage open(String url) {
        navigateTo(url);
        WaitUtils.waitForVisibility(LOGIN_LOGO);
        log("Login page opened: " + url);
        return this;
    }

    /**
     * Enters the username in the login input field.
     *
     * @param username the username to enter
     * @return this LoginPage
     */
    public LoginPage enterUsername(String username) {
        type(USERNAME_INPUT, username, "Username field");
        return this;
    }

    /**
     * Enters the password in the password input field.
     *
     * @param password the password to enter
     * @return this LoginPage
     */
    public LoginPage enterPassword(String password) {
        type(PASSWORD_INPUT, password, "Password field");
        return this;
    }

    /**
     * Clicks the Login button and returns the resulting InventoryPage.
     * Assumes login will succeed — caller should verify navigation.
     *
     * @return the InventoryPage after login
     */
    public InventoryPage clickLogin() {
        click(LOGIN_BUTTON, "Login button");
        return new InventoryPage();
    }

    /**
     * Clicks the Login button when expecting a failure (e.g., invalid credentials).
     * Returns this LoginPage as navigation does not occur.
     *
     * @return this LoginPage
     */
    public LoginPage clickLoginExpectingFailure() {
        click(LOGIN_BUTTON, "Login button (expecting failure)");
        return this;
    }

    /**
     * Full login workflow — enters credentials and clicks Login.
     *
     * @param username the username
     * @param password the password
     * @return the InventoryPage after successful login
     */
    public InventoryPage loginAs(String username, String password) {
        log("Attempting login as: " + username);
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    /**
     * Full login workflow returning this page — use when expecting failure.
     *
     * @param username the username
     * @param password the password
     * @return this LoginPage (remaining on login page after failure)
     */
    public LoginPage loginAsExpectingFailure(String username, String password) {
        log("Attempting login as (expecting failure): " + username);
        enterUsername(username);
        enterPassword(password);
        return clickLoginExpectingFailure();
    }

    // ─────────────────────────────────────────────────────────────
    // Assertions helpers (state readers for test validation)
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the error message text displayed after a failed login attempt.
     *
     * @return error message string
     */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE, "Login error message");
    }

    /**
     * Returns true if an error message is currently displayed.
     *
     * @return true if error is visible
     */
    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    /**
     * Returns true if currently on the login page (logo is visible).
     *
     * @return true if login page is loaded
     */
    public boolean isOnLoginPage() {
        return isDisplayed(LOGIN_LOGO);
    }
}
