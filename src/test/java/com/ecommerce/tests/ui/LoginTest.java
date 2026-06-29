package com.ecommerce.tests.ui;

import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.pages.InventoryPage;
import com.ecommerce.framework.pages.LoginPage;
import com.ecommerce.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginTest — Verifies the SauceDemo login functionality.
 *
 * <p>Covers: valid login, invalid credentials, locked user (negative testing).
 *
 * <p>Tags:
 * <ul>
 *   <li>smoke — included in fast pre-deployment checks</li>
 *   <li>regression — included in full test runs</li>
 *   <li>ui — UI layer tests</li>
 * </ul>
 */
@Tag(FrameworkConstants.TAG_UI)
@Tag(FrameworkConstants.TAG_REGRESSION)
class LoginTest extends BaseTest {

    @Test
    @Tag(FrameworkConstants.TAG_SMOKE)
    @DisplayName("Valid login redirects to Inventory page")
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Arrange
        LoginPage loginPage = new LoginPage();

        // Act
        InventoryPage inventoryPage = loginPage
                .enterUsername(FrameworkConstants.STANDARD_USER)
                .enterPassword(FrameworkConstants.DEFAULT_PASSWORD)
                .clickLogin();

        // Assert
        assertThat(inventoryPage.isInventoryLoaded())
                .as("Inventory list should be visible after login")
                .isTrue();

        assertThat(inventoryPage.getPageTitle())
                .as("Page title should be 'Products'")
                .isEqualTo("Products");

        assertThat(driver.getCurrentUrl())
                .as("URL should contain '/inventory.html'")
                .contains("inventory.html");
    }

    @Test
    @Tag(FrameworkConstants.TAG_NEGATIVE)
    @DisplayName("Invalid credentials show error message")
    void shouldDisplayErrorForInvalidCredentials() {
        // Arrange
        LoginPage loginPage = new LoginPage();

        // Act
        loginPage.loginAsExpectingFailure("invalid_user", "wrong_password");

        // Assert
        assertThat(loginPage.isErrorDisplayed())
                .as("Error message should be displayed for invalid credentials")
                .isTrue();

        assertThat(loginPage.getErrorMessage())
                .as("Error message should mention invalid credentials")
                .contains("Username and password do not match");

        assertThat(driver.getCurrentUrl())
                .as("Should remain on login page after failed login")
                .doesNotContain("inventory.html");
    }

    @Test
    @Tag(FrameworkConstants.TAG_NEGATIVE)
    @DisplayName("Locked-out user shows appropriate error")
    void shouldDisplayErrorForLockedOutUser() {
        // Arrange
        LoginPage loginPage = new LoginPage();

        // Act
        loginPage.loginAsExpectingFailure(
                FrameworkConstants.LOCKED_USER,
                FrameworkConstants.DEFAULT_PASSWORD
        );

        // Assert
        assertThat(loginPage.isErrorDisplayed())
                .as("Error message should be displayed for locked user")
                .isTrue();

        assertThat(loginPage.getErrorMessage())
                .as("Error should mention locked out")
                .containsIgnoringCase("locked out");
    }

    @Test
    @Tag(FrameworkConstants.TAG_NEGATIVE)
    @DisplayName("Empty credentials show validation error")
    void shouldDisplayErrorForEmptyCredentials() {
        // Arrange
        LoginPage loginPage = new LoginPage();

        // Act — click without entering anything
        loginPage.clickLoginExpectingFailure();

        // Assert
        assertThat(loginPage.isErrorDisplayed())
                .as("Validation error should appear for empty fields")
                .isTrue();

        assertThat(loginPage.getErrorMessage())
                .as("Error should mention username is required")
                .containsIgnoringCase("Username is required");
    }

    @Test
    @Tag(FrameworkConstants.TAG_SMOKE)
    @DisplayName("Login page loads with correct title")
    void shouldDisplayCorrectPageTitle() {
        assertThat(driver.getTitle())
                .as("Browser tab title should be 'Swag Labs'")
                .isEqualTo("Swag Labs");
    }
}
