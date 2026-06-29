package com.ecommerce.framework.pages;

import com.ecommerce.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CartPage — Page Object for the SauceDemo shopping cart page.
 * URL: https://www.saucedemo.com/cart.html
 */
public class CartPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────
    private static final By PAGE_TITLE         = By.className("title");
    private static final By CART_ITEMS         = By.className("cart_item");
    private static final By ITEM_NAMES         = By.className("inventory_item_name");
    private static final By ITEM_PRICES        = By.className("inventory_item_price");
    private static final By CONTINUE_SHOPPING  = By.id("continue-shopping");
    private static final By CHECKOUT_BUTTON    = By.id("checkout");
    private static final By REMOVE_BUTTON      = By.cssSelector(".cart_item button");

    // ─────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Waits for the cart page to load and verifies the title.
     *
     * @return this CartPage
     */
    public CartPage waitForPageToLoad() {
        WaitUtils.waitForVisibility(PAGE_TITLE);
        log("Cart page loaded");
        return this;
    }

    /**
     * Clicks the Checkout button to proceed to checkout.
     *
     * @return CheckoutPage
     */
    public CheckoutPage proceedToCheckout() {
        click(CHECKOUT_BUTTON, "Checkout button");
        return new CheckoutPage();
    }

    /**
     * Clicks Continue Shopping to return to the inventory.
     *
     * @return InventoryPage
     */
    public InventoryPage continueShopping() {
        click(CONTINUE_SHOPPING, "Continue Shopping button");
        return new InventoryPage();
    }

    /**
     * Removes the first item from the cart.
     *
     * @return this CartPage
     */
    public CartPage removeFirstItem() {
        click(REMOVE_BUTTON, "Remove first cart item");
        return this;
    }

    // ─────────────────────────────────────────────────────────────
    // State Readers
    // ─────────────────────────────────────────────────────────────

    /** Returns the page title text. */
    public String getPageTitle() {
        return getText(PAGE_TITLE, "Cart page title");
    }

    /** Returns the number of items in the cart. */
    public int getCartItemCount() {
        return findAll(CART_ITEMS).size();
    }

    /** Returns list of item names in the cart. */
    public List<String> getCartItemNames() {
        WaitUtils.waitForVisibility(PAGE_TITLE);
        return findAll(ITEM_NAMES).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /** Returns true if the cart is empty (no items present). */
    public boolean isCartEmpty() {
        return findAll(CART_ITEMS).isEmpty();
    }

    /** Returns true if a specific product is in the cart by name. */
    public boolean isProductInCart(String productName) {
        return getCartItemNames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(productName));
    }
}
