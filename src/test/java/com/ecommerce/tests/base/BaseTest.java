package com.ecommerce.tests.base;

import com.ecommerce.framework.config.ConfigReader;
import com.ecommerce.framework.drivers.DriverFactory;
import com.ecommerce.framework.listeners.TestListener;
import com.ecommerce.framework.reports.ExtentTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

/**
 * BaseTest — Parent class for all UI test classes.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Initialize WebDriver before each test via DriverFactory</li>
 *   <li>Navigate to the application base URL</li>
 *   <li>Quit WebDriver after each test (even if the test fails)</li>
 *   <li>Expose commonly used config values to subclasses</li>
 * </ul>
 *
 * <p>@ExtendWith(TestListener.class) hooks Extent Reports into every test lifecycle event.
 *
 * <p>Design: BaseTest does NOT contain assertions — it is purely infrastructure.
 */
@ExtendWith(TestListener.class)
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected ConfigReader config = ConfigReader.getInstance();

    // Exposed for convenience in subclasses
    protected String baseUrl;
    protected WebDriver driver;

    /**
     * Runs before each test method:
     * <ol>
     *   <li>Reads base URL from configuration</li>
     *   <li>Initializes thread-local WebDriver</li>
     *   <li>Navigates to the base URL</li>
     * </ol>
     *
     * @param testInfo JUnit 5 TestInfo providing test name and tags
     */
    @BeforeEach
    public void setUp(TestInfo testInfo) {
        baseUrl = config.getProperty("app.base.url");
        log.info("╔══════════════════════════════════════════════════");
        log.info("║  TEST START: {}", testInfo.getDisplayName());
        log.info("║  Base URL: {}", baseUrl);
        log.info("╚══════════════════════════════════════════════════");

        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
        driver.get(baseUrl);

        ExtentTestManager.logInfo("Browser launched and navigated to: " + baseUrl);
    }

    /**
     * Runs after each test method (regardless of pass/fail):
     * <ol>
     *   <li>Quits the WebDriver and removes from ThreadLocal</li>
     * </ol>
     *
     * @param testInfo JUnit 5 TestInfo providing test name
     */
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        log.info("╔══════════════════════════════════════════════════");
        log.info("║  TEST END: {}", testInfo.getDisplayName());
        log.info("╚══════════════════════════════════════════════════");
        DriverFactory.quitDriver();
    }
}
