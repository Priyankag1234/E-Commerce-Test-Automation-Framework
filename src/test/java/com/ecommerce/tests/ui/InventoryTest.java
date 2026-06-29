package com.ecommerce.tests.ui;

import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.pages.InventoryPage;
import com.ecommerce.framework.pages.LoginPage;
import com.ecommerce.tests.base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InventoryTest — Verifies product listing and sorting functionality.
 */
@Tag(FrameworkConstants.TAG_UI)
@Tag(FrameworkConstants.TAG_REGRESSION)
class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void loginAndNavigate() {
        inventoryPage = new LoginPage()
                .loginAs(FrameworkConstants.STANDARD_USER, FrameworkConstants.DEFAULT_PASSWORD)
                .waitForPageToLoad();
    }

    @Test
    @Tag(FrameworkConstants.TAG_SMOKE)
    @DisplayName("Inventory page loads 6 products")
    void shouldDisplaySixProducts() {
        int productCount = inventoryPage.getProductCount();

        assertThat(productCount)
                .as("SauceDemo should always display exactly 6 products")
                .isEqualTo(6);
    }

    @Test
    @Tag(FrameworkConstants.TAG_SMOKE)
    @DisplayName("Product listing page has correct title")
    void shouldDisplayCorrectInventoryTitle() {
        assertThat(inventoryPage.getPageTitle())
                .as("Page title should be 'Products'")
                .isEqualTo("Products");
    }

    @Test
    @DisplayName("Sort by Price (low to high) orders products correctly")
    void shouldSortByPriceLowToHigh() {
        inventoryPage.sortBy("Price (low to high)");

        List<Double> prices = inventoryPage.getAllProductPrices();

        assertThat(prices)
                .as("Prices should be in ascending order after sorting low-to-high")
                .isSorted();
    }

    @Test
    @DisplayName("Sort by Price (high to low) orders products correctly")
    void shouldSortByPriceHighToLow() {
        inventoryPage.sortBy("Price (high to low)");

        List<Double> prices = inventoryPage.getAllProductPrices();

        assertThat(prices)
                .as("Prices should be in descending order after sorting high-to-low")
                .isSortedAccordingTo((a, b) -> Double.compare(b, a));
    }

    @Test
    @DisplayName("Sort by Name (A to Z) orders products alphabetically")
    void shouldSortByNameAToZ() {
        inventoryPage.sortBy("Name (A to Z)");

        List<String> names = inventoryPage.getAllProductNames();

        assertThat(names)
                .as("Product names should be in alphabetical ascending order")
                .isSorted();
    }

    @Test
    @DisplayName("Sort by Name (Z to A) orders products reverse alphabetically")
    void shouldSortByNameZToA() {
        inventoryPage.sortBy("Name (Z to A)");

        List<String> names = inventoryPage.getAllProductNames();

        assertThat(names)
                .as("Product names should be in reverse alphabetical order")
                .isSortedAccordingTo((a, b) -> b.compareToIgnoreCase(a));
    }

    @Test
    @DisplayName("Cart badge shows correct count after adding one item")
    void shouldIncrementCartBadgeAfterAddingItem() {
        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart should be empty initially")
                .isEqualTo(0);

        inventoryPage.addFirstItemToCart();

        assertThat(inventoryPage.getCartBadgeCount())
                .as("Cart badge should show 1 after adding one item")
                .isEqualTo(1);
    }
}
