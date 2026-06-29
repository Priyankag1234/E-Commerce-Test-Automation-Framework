package com.ecommerce.framework.pages.components;

import com.ecommerce.framework.pages.BasePage;
import org.openqa.selenium.By;

/**
 * HeaderComponent — Reusable component for the SauceDemo navigation header.
 *
 * <p>Composition over Inheritance — Pages that need header actions
 *    create an instance of this component rather than inheriting from it.
 *
 * <p>Example usage:
 * <pre>
 *     HeaderComponent header = new HeaderComponent();
 *     header.openMenu();
 *     header.logout();
 * </pre>
 */
public class HeaderComponent extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────
    private static final By MENU_BUTTON         = By.id("react-burger-menu-btn");
    private static final By MENU_CLOSE_BUTTON   = By.id("react-burger-cross-btn");
    private static final By LOGOUT_LINK         = By.id("logout_sidebar_link");
    private static final By ALL_ITEMS_LINK      = By.id("inventory_sidebar_link");
    private static final By ABOUT_LINK          = By.id("about_sidebar_link");
    private static final By RESET_LINK          = By.id("reset_sidebar_link");
    private static final By APP_LOGO            = By.className("app_logo");
    private static final By CART_BADGE          = By.className("shopping_cart_badge");

    // ─────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Opens the hamburger navigation menu.
     *
     * @return this HeaderComponent
     */
    public HeaderComponent openMenu() {
        click(MENU_BUTTON, "Hamburger menu button");
        return this;
    }

    /**
     * Closes the navigation menu.
     *
     * @return this HeaderComponent
     */
    public HeaderComponent closeMenu() {
        click(MENU_CLOSE_BUTTON, "Menu close button");
        return this;
    }

    /**
     * Logs out the current user by opening the menu and clicking Logout.
     */
    public void logout() {
        openMenu();
        click(LOGOUT_LINK, "Logout link");
        log("User logged out via header menu");
    }

    /**
     * Resets the app state via the menu Reset App State option.
     *
     * @return this HeaderComponent
     */
    public HeaderComponent resetAppState() {
        openMenu();
        click(RESET_LINK, "Reset App State link");
        closeMenu();
        log("App state reset via header menu");
        return this;
    }

    // ─────────────────────────────────────────────────────────────
    // State Readers
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the app logo text.
     *
     * @return logo text string
     */
    public String getLogoText() {
        return getText(APP_LOGO, "App logo");
    }

    /**
     * Returns the cart badge count from the header icon.
     *
     * @return count as int, 0 if badge is not displayed
     */
    public int getCartItemCount() {
        if (!isDisplayed(CART_BADGE)) return 0;
        return Integer.parseInt(getText(CART_BADGE, "Cart badge"));
    }
}
