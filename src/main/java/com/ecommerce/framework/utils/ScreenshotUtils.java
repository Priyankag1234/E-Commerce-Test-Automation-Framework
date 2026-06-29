package com.ecommerce.framework.utils;

import com.ecommerce.framework.config.FrameworkConstants;
import com.ecommerce.framework.drivers.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtils — Captures screenshots from the current WebDriver session.
 *
 * <p>Provides two formats:
 * <ul>
 *   <li>Base64 string — for embedding directly into Extent Reports</li>
 *   <li>File path — for saving as a PNG artifact to disk</li>
 * </ul>
 */
public final class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    private ScreenshotUtils() {}

    /**
     * Captures a screenshot and returns it as a Base64-encoded string.
     * Used for embedding images directly into Extent Report HTML.
     *
     * @return Base64 screenshot string or empty string if capture fails
     */
    public static String captureAsBase64() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                log.warn("Cannot capture screenshot — driver is null for thread [{}]", Thread.currentThread().getName());
                return "";
            }
            String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            log.debug("Screenshot captured as Base64 for thread [{}]", Thread.currentThread().getName());
            return base64;
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Captures a screenshot and saves it as a PNG file to the screenshots directory.
     *
     * @param testName the name of the failing test (used in filename)
     * @return absolute path to the saved screenshot file
     */
    public static String captureAndSave(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                log.warn("Cannot save screenshot — driver is null.");
                return "";
            }

            // Ensure screenshots directory exists
            Path screenshotDir = Paths.get(FrameworkConstants.SCREENSHOT_DIR);
            Files.createDirectories(screenshotDir);

            // Generate a unique timestamped filename
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String filename = safeName + "_" + timestamp + ".png";
            Path filePath = screenshotDir.resolve(filename);

            // Capture and save
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), filePath);

            log.info("Screenshot saved: {}", filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("Failed to save screenshot for test [{}]: {}", testName, e.getMessage());
            return "";
        }
    }
}
