package com.ecommerce.tests.ui;

import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.pages.CartPage;
import com.ecommerce.framework.pages.InventoryPage;
import com.ecommerce.framework.pages.LoginPage;
import com.ecommerce.tests.base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CartTest — Verifies shopping cart add, remove, and navigation functionality.
 */
@Tag(FrameworkConstants.TAG_UI)
@Tag(FrameworkConstants.TAG_REGRESSION)
class CartTest extends BaseTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = new LoginPage()
                .loginAs(FrameworkConstants.STANDARD_USER, FrameworkConstants.DEFAULT_PASSWORD)
                .waitForPageToLoad();
    }

    @Test
    @Tag(FrameworkConstants.TAG_SMOKE)
    @DisplayName("Adding product to cart updates cart badge")
    void shouldUpdateCartBadgeWhenProductAdded() {
        inventoryPage.addToCartByName(PRODUCT_NAME);

        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 1 after adding one item")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Product is present in cart after adding from inventory")
    void shouldShowProductInCart() {
        inventoryPage.addToCartByName(PRODUCT_NAME);

        CartPage cartPage = inventoryPage.goToCart().waitForPageToLoad();

        assertThat(cartPage.isProductInCart(PRODUCT_NAME))
                .as("The added product should appear in the cart")
                .isTrue();
    }

    @Test
    @DisplayName("Cart shows correct item count after adding multiple products")
    void shouldShowCorrectCountForMultipleItems() {
        inventoryPage
                .addToCartByName("Sauce Labs Backpack")
                .addToCartByName("Sauce Labs Bike Light");

        CartPage cartPage = inventoryPage.goToCart().waitForPageToLoad();

        assertThat(cartPage.getCartItemCount())
                .as("Cart should contain 2 items")
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Cart page displays correct title")
    void shouldDisplayCorrectCartTitle() {
        inventoryPage.addFirstItemToCart();

        CartPage cartPage = inventoryPage.goToCart().waitForPageToLoad();

        assertThat(cartPage.getPageTitle())
                .as("Cart page title should be 'Your Cart'")
                .isEqualTo("Your Cart");
    }

    @Test
    @DisplayName("Continue Shopping returns to inventory page")
    void shouldReturnToInventoryOnContinueShopping() {
        inventoryPage.addFirstItemToCart();
        CartPage cartPage = inventoryPage.goToCart().waitForPageToLoad();
        InventoryPage backToInventory = cartPage.continueShopping();

        assertThat(backToInventory.isInventoryLoaded())
                .as("Should return to inventory listing page")
                .isTrue();
    }

    @Test
    @DisplayName("Removing item from cart reduces item count")
    void shouldReduceCartCountAfterRemoval() {
        inventoryPage.addFirstItemToCart();
        CartPage cartPage = inventoryPage.goToCart().waitForPageToLoad();

        assertThat(cartPage.getCartItemCount())
                .as("Cart should have 1 item before removal")
                .isEqualTo(1);

        cartPage.removeFirstItem();

        assertThat(cartPage.isCartEmpty())
                .as("Cart should be empty after removing the only item")
                .isTrue();
    }
}
