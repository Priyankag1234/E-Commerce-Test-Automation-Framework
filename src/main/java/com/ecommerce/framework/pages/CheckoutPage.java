package com.ecommerce.framework.pages;

import com.ecommerce.framework.utils.WaitUtils;
import org.openqa.selenium.By;

/**
 * CheckoutPage — Page Object for the SauceDemo multi-step checkout flow.
 *
 * <p>Step 1: Checkout Information (first name, last name, zip code)
 * <p>Step 2: Checkout Overview (order summary)
 * <p>Step 3: Order Complete (confirmation)
 */
public class CheckoutPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators — Step 1: Information
    // ─────────────────────────────────────────────────────────────
    private static final By PAGE_TITLE     = By.className("title");
    private static final By FIRST_NAME     = By.id("first-name");
    private static final By LAST_NAME      = By.id("last-name");
    private static final By ZIP_CODE       = By.id("postal-code");
    private static final By CONTINUE_BTN   = By.id("continue");
    private static final By ERROR_MESSAGE  = By.cssSelector("[data-test='error']");

    // ─────────────────────────────────────────────────────────────
    // Locators — Step 2: Overview
    // ─────────────────────────────────────────────────────────────
    private static final By FINISH_BTN         = By.id("finish");
    private static final By CANCEL_BTN         = By.id("cancel");
    private static final By ITEM_TOTAL_LABEL   = By.className("summary_subtotal_label");
    private static final By TOTAL_LABEL        = By.className("summary_total_label");

    // ─────────────────────────────────────────────────────────────
    // Locators — Step 3: Confirmation
    // ─────────────────────────────────────────────────────────────
    private static final By COMPLETE_HEADER    = By.className("complete-header");
    private static final By COMPLETE_TEXT      = By.className("complete-text");
    private static final By BACK_HOME_BTN      = By.id("back-to-products");

    // ─────────────────────────────────────────────────────────────
    // Step 1 Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Waits for the checkout information page to load.
     *
     * @return this CheckoutPage
     */
    public CheckoutPage waitForPageToLoad() {
        WaitUtils.waitForVisibility(FIRST_NAME);
        log("Checkout Step 1 — Information page loaded");
        return this;
    }

    /**
     * Fills in the checkout form and proceeds.
     *
     * @param firstName customer first name
     * @param lastName  customer last name
     * @param zipCode   postal code
     * @return this CheckoutPage (now on Step 2 — Overview)
     */
    public CheckoutPage fillCheckoutInfo(String firstName, String lastName, String zipCode) {
        type(FIRST_NAME, firstName, "First name field");
        type(LAST_NAME, lastName, "Last name field");
        type(ZIP_CODE, zipCode, "ZIP code field");
        click(CONTINUE_BTN, "Continue button");
        log("Checkout information submitted for: " + firstName + " " + lastName);
        return this;
    }

    // ─────────────────────────────────────────────────────────────
    // Step 2 Actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Waits for the checkout overview (Step 2) page to load.
     *
     * @return this CheckoutPage
     */
    public CheckoutPage waitForOverviewToLoad() {
        WaitUtils.waitForVisibility(FINISH_BTN);
        log("Checkout Step 2 — Overview page loaded");
        return this;
    }

    /**
     * Completes the order from the overview page.
     *
     * @return this CheckoutPage (now on Step 3 — Confirmation)
     */
    public CheckoutPage finishOrder() {
        WaitUtils.waitForVisibility(FINISH_BTN);
        click(FINISH_BTN, "Finish button");
        log("Order finalized");
        return this;
    }

    /**
     * Returns the item total from the overview page.
     * Waits for the overview to load first.
     *
     * @return item total label text (e.g., "Item total: $29.99")
     */
    public String getItemTotal() {
        WaitUtils.waitForVisibility(ITEM_TOTAL_LABEL);
        return getText(ITEM_TOTAL_LABEL, "Item total label");
    }

    /**
     * Returns the grand total from the overview page.
     *
     * @return total label text (e.g., "Total: $32.39")
     */
    public String getGrandTotal() {
        WaitUtils.waitForVisibility(TOTAL_LABEL);
        return getText(TOTAL_LABEL, "Grand total label");
    }

    // ─────────────────────────────────────────────────────────────
    // Step 3 State Readers
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the order complete header message.
     *
     * @return "Thank you for your order!" or similar
     */
    public String getOrderCompleteHeader() {
        WaitUtils.waitForVisibility(COMPLETE_HEADER);
        return getText(COMPLETE_HEADER, "Order complete header");
    }

    /**
     * Returns the confirmation body text.
     *
     * @return order confirmation detail text
     */
    public String getOrderCompleteText() {
        return getText(COMPLETE_TEXT, "Order complete text");
    }

    /**
     * Returns true if the order confirmation page is displayed.
     *
     * @return true if confirmation header is visible
     */
    public boolean isOrderComplete() {
        return isDisplayed(COMPLETE_HEADER);
    }

    /**
     * Returns the current page title.
     *
     * @return title text
     */
    public String getPageTitle() {
        return getText(PAGE_TITLE, "Checkout page title");
    }

    /**
     * Returns the error message displayed on invalid form submission.
     *
     * @return error message string
     */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE, "Checkout error message");
    }

    /**
     * Clicks Back Home button from confirmation page.
     *
     * @return InventoryPage
     */
    public InventoryPage backToHome() {
        click(BACK_HOME_BTN, "Back to Products button");
        return new InventoryPage();
    }
}
