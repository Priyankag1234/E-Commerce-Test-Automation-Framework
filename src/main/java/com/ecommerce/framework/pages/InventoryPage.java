package com.ecommerce.framework.pages;

import com.ecommerce.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * InventoryPage — Page Object for the SauceDemo product listing page.
 * URL: https://www.saucedemo.com/inventory.html
 *
 * <p>Encapsulates: product listing, sorting, and add-to-cart functionality.
 */
public class InventoryPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────
    private static final By PAGE_TITLE        = By.className("title");
    private static final By INVENTORY_LIST    = By.className("inventory_list");
    private static final By INVENTORY_ITEMS   = By.className("inventory_item");
    private static final By SORT_DROPDOWN     = By.className("product_sort_container");
    private static final By ITEM_NAMES        = By.className("inventory_item_name");
    private static final By ITEM_PRICES       = By.className("inventory_item_price");
    private static final By CART_BADGE        = By.className("shopping_cart_badge");
    private static final By CART_LINK         = By.className("shopping_cart_link");

    private static final String ADD_TO_CART_BY_NAME = "//div[text()='%s']/ancestor::div[@class='inventory_item']//button";

    // ─────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Waits for the inventory page to fully load.
     *
     * @return this InventoryPage
     */
    public InventoryPage waitForPageToLoad() {
        WaitUtils.waitForVisibility(INVENTORY_LIST);
        log("Inventory page loaded successfully");
        return this;
    }

    /**
     * Sorts the product list using the sort dropdown.
     *
     * @param sortOption the visible text option (e.g., "Price (low to high)")
     * @return this InventoryPage
     */
    public InventoryPage sortBy(String sortOption) {
        selectByVisibleText(SORT_DROPDOWN, sortOption, "Sort dropdown");
        log("Products sorted by: " + sortOption);
        return this;
    }

    /**
     * Adds a product to the cart by its display name.
     *
     * @param productName the exact product name as displayed on the page
     * @return this InventoryPage
     */
    public InventoryPage addToCartByName(String productName) {
        By addToCartBtn = By.xpath(String.format(ADD_TO_CART_BY_NAME, productName));
        click(addToCartBtn, "Add to Cart for: " + productName);
        log("Added to cart: " + productName);
        return this;
    }

    /**
     * Clicks the first Add to Cart button on the page.
     *
     * @return this InventoryPage
     */
    public InventoryPage addFirstItemToCart() {
        By firstAddToCart = By.cssSelector(".inventory_item button");
        click(firstAddToCart, "First Add to Cart button");
        return this;
    }

    /**
     * Navigates to the shopping cart.
     *
     * @return CartPage
     */
    public CartPage goToCart() {
        click(CART_LINK, "Shopping Cart icon");
        return new CartPage();
    }

    // ─────────────────────────────────────────────────────────────
    // State Readers
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the page title text.
     *
     * @return "Products" or similar
     */
    public String getPageTitle() {
        return getText(PAGE_TITLE, "Inventory page title");
    }

    /**
     * Returns the number of product items displayed.
     *
     * @return count of inventory items
     */
    public int getProductCount() {
        WaitUtils.waitForVisibility(INVENTORY_LIST);
        return findAll(INVENTORY_ITEMS).size();
    }

    /**
     * Returns the list of all product names displayed.
     *
     * @return list of product name strings
     */
    public List<String> getAllProductNames() {
        WaitUtils.waitForVisibility(INVENTORY_LIST);
        return findAll(ITEM_NAMES).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns the list of all product prices as doubles.
     *
     * @return list of prices
     */
    public List<Double> getAllProductPrices() {
        WaitUtils.waitForVisibility(INVENTORY_LIST);
        return findAll(ITEM_PRICES).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "")))
                .collect(Collectors.toList());
    }

    /**
     * Returns the cart badge count (number shown on the cart icon).
     *
     * @return cart item count, or 0 if badge is not displayed
     */
    public int getCartBadgeCount() {
        if (!isDisplayed(CART_BADGE)) return 0;
        return Integer.parseInt(getText(CART_BADGE, "Cart badge"));
    }

    /**
     * Returns true if the inventory list is displayed.
     *
     * @return true if inventory is visible
     */
    public boolean isInventoryLoaded() {
        return isDisplayed(INVENTORY_LIST);
    }
}
