package com.ecommerce.framework.drivers;

import com.ecommerce.framework.config.ConfigReader;
import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * DriverFactory — Thread-safe WebDriver lifecycle manager.
 *
 * <p>Design Pattern: ThreadLocal ensures each parallel test thread has its own
 *    isolated WebDriver instance, preventing cross-thread browser state pollution.
 *
 * <p>Supports:
 * <ul>
 *   <li>Local execution using WebDriverManager (auto-downloads browser drivers)</li>
 *   <li>Remote execution on Selenium Grid via RemoteWebDriver</li>
 *   <li>Headless mode for CI/CD environments</li>
 *   <li>Dynamic browser switching via -Dbrowser system property</li>
 * </ul>
 *
 * <p>Usage: DriverFactory.getDriver() — always returns the driver for the current thread.
 */
public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    /**
     * ThreadLocal ensures each thread maintains its own independent WebDriver reference.
     * Critical for parallel execution — without this, multiple threads would share
     * one browser causing race conditions and test interference.
     */
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Private constructor — static utility class, not instantiable
    private DriverFactory() {}

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the WebDriver for the current thread.
     * Creates a new instance if none exists for this thread.
     *
     * @return thread-local WebDriver instance
     */
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            log.warn("No driver found for thread [{}]. Initializing a new driver.", Thread.currentThread().getName());
            initDriver();
        }
        return driverThreadLocal.get();
    }

    /**
     * Initializes the WebDriver for the current thread.
     * Reads browser and execution mode from config/system properties.
     */
    public static void initDriver() {
        String browser       = resolveBrowser();
        String executionMode = resolveExecutionMode();
        boolean headless     = resolveHeadless();

        log.info("Initializing driver | Thread: [{}] | Browser: [{}] | Mode: [{}] | Headless: [{}]",
                Thread.currentThread().getName(), browser, executionMode, headless);

        WebDriver driver;

        if (FrameworkConstants.REMOTE.equalsIgnoreCase(executionMode)) {
            driver = createRemoteDriver(browser, headless);
        } else {
            driver = BrowserFactory.createLocalDriver(browser, headless);
        }

        configureDriver(driver);
        driverThreadLocal.set(driver);

        log.info("Driver initialized successfully for thread [{}]", Thread.currentThread().getName());
    }

    /**
     * Quits the current thread's WebDriver and removes it from ThreadLocal.
     * MUST be called in @AfterEach to prevent browser process leaks.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            log.info("Quitting driver for thread [{}]", Thread.currentThread().getName());
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("Exception while quitting driver for thread [{}]: {}", Thread.currentThread().getName(), e.getMessage());
            } finally {
                driverThreadLocal.remove(); // Critical: prevent memory leak in thread pools
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a RemoteWebDriver pointed at the Selenium Grid Hub.
     *
     * @param browser  browser name (chrome, firefox, edge)
     * @param headless whether to run headless
     * @return configured RemoteWebDriver
     */
    private static WebDriver createRemoteDriver(String browser, boolean headless) {
        String gridUrlStr = resolveGridUrl();
        log.info("Connecting to Selenium Grid at: {}", gridUrlStr);

        try {
            URL gridUrl = new URL(gridUrlStr);
            return new RemoteWebDriver(gridUrl, BrowserFactory.getCapabilities(browser, headless));
        } catch (MalformedURLException e) {
            throw new FrameworkException("Invalid Selenium Grid URL: [" + gridUrlStr + "]", e);
        }
    }

    /**
     * Applies standard driver configuration: timeouts, window size.
     *
     * @param driver the WebDriver to configure
     */
    private static void configureDriver(WebDriver driver) {
        ConfigReader config = ConfigReader.getInstance();

        // Page load timeout
        int pageLoadTimeout = config.getIntProperty("page.load.timeout");
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        // Implicit wait — intentionally set to 0 to rely exclusively on explicit waits.
        // Mixing implicit and explicit waits causes unpredictable timing issues.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        // Maximize window (unless headless — window size set in browser options)
        boolean shouldMaximize = config.getBooleanProperty("browser.window.maximize");
        if (shouldMaximize) {
            driver.manage().window().maximize();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Property Resolvers (JVM System Property → Config File)
    // ─────────────────────────────────────────────────────────────

    private static String resolveBrowser() {
        String sysProp = System.getProperty("browser");
        if (sysProp != null && !sysProp.isBlank()) return sysProp.toLowerCase();
        return ConfigReader.getInstance().getProperty("browser", FrameworkConstants.CHROME);
    }

    private static String resolveExecutionMode() {
        String sysProp = System.getProperty("execution");
        if (sysProp != null && !sysProp.isBlank()) return sysProp.toLowerCase();
        return ConfigReader.getInstance().getProperty("execution.mode", FrameworkConstants.LOCAL);
    }

    private static boolean resolveHeadless() {
        String sysProp = System.getProperty("headless");
        if (sysProp != null && !sysProp.isBlank()) return Boolean.parseBoolean(sysProp);
        return ConfigReader.getInstance().getBooleanProperty("headless");
    }

    private static String resolveGridUrl() {
        String sysProp = System.getProperty("gridUrl");
        if (sysProp != null && !sysProp.isBlank()) return sysProp;
        return ConfigReader.getInstance().getProperty("grid.url", "http://localhost:4444");
    }
}
