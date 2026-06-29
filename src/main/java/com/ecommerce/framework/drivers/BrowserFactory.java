package com.ecommerce.framework.drivers;

import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.exceptions.FrameworkException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;

/**
 * BrowserFactory — Creates browser-specific WebDriver instances.
 *
 * <p>Design Pattern: Factory — Decouples browser selection logic from test code.
 *    Adding a new browser requires only extending this class, not modifying tests.
 *
 * <p>Each browser method:
 * <ul>
 *   <li>Configures browser-specific options (security flags, window size, headless)</li>
 *   <li>Uses WebDriverManager for automatic binary management</li>
 *   <li>Returns a typed WebDriver polymorphically</li>
 * </ul>
 */
public final class BrowserFactory {

    private static final Logger log = LogManager.getLogger(BrowserFactory.class);

    private BrowserFactory() {}

    // ─────────────────────────────────────────────────────────────
    // Public Factory Methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates and returns a local WebDriver for the specified browser.
     *
     * @param browser  browser name (chrome, firefox, edge)
     * @param headless whether to run in headless mode
     * @return configured WebDriver instance
     */
    public static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser.toLowerCase()) {
            case FrameworkConstants.CHROME  -> createChromeDriver(headless);
            case FrameworkConstants.FIREFOX -> createFirefoxDriver(headless);
            case FrameworkConstants.EDGE    -> createEdgeDriver(headless);
            default -> throw new FrameworkException(
                    "Unsupported browser: [" + browser + "]. Supported values: chrome, firefox, edge."
            );
        };
    }

    /**
     * Returns browser capabilities/options for RemoteWebDriver (Selenium Grid).
     *
     * @param browser  browser name
     * @param headless whether to use headless mode
     * @return AbstractDriverOptions compatible with RemoteWebDriver
     */
    public static AbstractDriverOptions<?> getCapabilities(String browser, boolean headless) {
        return switch (browser.toLowerCase()) {
            case FrameworkConstants.CHROME  -> buildChromeOptions(headless);
            case FrameworkConstants.FIREFOX -> buildFirefoxOptions(headless);
            case FrameworkConstants.EDGE    -> buildEdgeOptions(headless);
            default -> throw new FrameworkException("Unsupported browser for remote execution: [" + browser + "]");
        };
    }

    // ─────────────────────────────────────────────────────────────
    // Chrome
    // ─────────────────────────────────────────────────────────────

    private static WebDriver createChromeDriver(boolean headless) {
        log.info("Setting up ChromeDriver via WebDriverManager");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildChromeOptions(headless);
        log.info("ChromeDriver initialized | Headless: {}", headless);
        return new ChromeDriver(options);
    }

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");     // Newer headless mode (Selenium 4.6+)
            options.addArguments("--window-size=1920,1080");
        }

        // Security & stability flags for Docker/CI environments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        // Suppress Chrome logging noise
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-logging", "enable-automation"});

        return options;
    }

    // ─────────────────────────────────────────────────────────────
    // Firefox
    // ─────────────────────────────────────────────────────────────

    private static WebDriver createFirefoxDriver(boolean headless) {
        log.info("Setting up FirefoxDriver via WebDriverManager");
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = buildFirefoxOptions(headless);
        log.info("FirefoxDriver initialized | Headless: {}", headless);
        return new FirefoxDriver(options);
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }

        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("geo.enabled", false);

        return options;
    }

    // ─────────────────────────────────────────────────────────────
    // Edge
    // ─────────────────────────────────────────────────────────────

    private static WebDriver createEdgeDriver(boolean headless) {
        log.info("Setting up EdgeDriver via WebDriverManager");
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = buildEdgeOptions(headless);
        log.info("EdgeDriver initialized | Headless: {}", headless);
        return new EdgeDriver(options);
    }

    private static EdgeOptions buildEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-notifications");

        return options;
    }
}
