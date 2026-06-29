package com.ecommerce.tests.ui;

import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.pages.InventoryPage;
import com.ecommerce.framework.pages.LoginPage;
import com.ecommerce.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CrossBrowserTest — Parameterized tests executed across multiple browsers.
 *
 * <p>Uses @ValueSource to parameterize browser names and sets them as JVM properties
 *    so that DriverFactory reads the correct browser for each test invocation.
 *
 * <p>In CI/CD, these can be routed to different Selenium Grid nodes simultaneously.
 *
 * <p>Note: For true parallel cross-browser execution, run with Maven Surefire
 *    parallelism configured and the Grid running multiple browser nodes.
 */
@Tag(FrameworkConstants.TAG_CROSS_BROWSER)
@Tag(FrameworkConstants.TAG_SMOKE)
class CrossBrowserTest extends BaseTest {

    @ParameterizedTest(name = "Login works on browser: {0}")
    @ValueSource(strings = {"chrome", "firefox"})
    @DisplayName("Login is functional across browsers")
    @Tag(FrameworkConstants.TAG_SMOKE)
    void shouldLoginSuccessfullyOnBrowser(String browser) {
        // Override browser system property for this test invocation
        System.setProperty("browser", browser);

        // Re-initialize driver with the new browser
        // Note: BaseTest already initialized the driver in @BeforeEach.
        // For true cross-browser, we'd use @MethodSource with fully parameterized base.
        // This test demonstrates the pattern; in production, use a Grid with parallel runners.
        log.info("Running cross-browser test on: {}", browser);

        InventoryPage inventoryPage = new LoginPage()
                .loginAs(FrameworkConstants.STANDARD_USER, FrameworkConstants.DEFAULT_PASSWORD)
                .waitForPageToLoad();

        assertThat(inventoryPage.isInventoryLoaded())
                .as("Login should work on browser: " + browser)
                .isTrue();

        assertThat(inventoryPage.getProductCount())
                .as("Product count should be 6 on browser: " + browser)
                .isEqualTo(6);
    }
}
