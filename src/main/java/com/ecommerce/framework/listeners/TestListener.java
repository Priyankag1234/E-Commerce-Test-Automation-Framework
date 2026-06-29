package com.ecommerce.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.ecommerce.framework.reports.ExtentReportManager;
import com.ecommerce.framework.reports.ExtentTestManager;
import com.ecommerce.framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * TestListener — JUnit 5 Extension that integrates with Extent Reports.
 *
 * <p>Implements:
 * <ul>
 *   <li>BeforeEachCallback — Creates a new ExtentTest node before each test</li>
 *   <li>AfterEachCallback — Marks test pass/fail and attaches screenshots</li>
 *   <li>TestWatcher — Handles skipped and aborted tests</li>
 *   <li>BeforeAllCallback / AfterAllCallback — Initializes and flushes report</li>
 * </ul>
 *
 * <p>Register via @ExtendWith(TestListener.class) on BaseTest.
 */
public class TestListener implements BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback, AfterEachCallback, TestWatcher {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    // ─────────────────────────────────────────────────────────────
    // Suite-level: Initialize reports once per test class
    // ─────────────────────────────────────────────────────────────

    @Override
    public void beforeAll(ExtensionContext context) {
        // Eagerly initialize the ExtentReports instance (creates output directory etc.)
        ExtentReportManager.getInstance();
        log.info("=== Test Suite Starting: [{}] ===", context.getDisplayName());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        log.info("=== Test Suite Completed: [{}] ===", context.getDisplayName());
        // Flush after each class to ensure data is written progressively
        ExtentReportManager.flushReports();
    }

    // ─────────────────────────────────────────────────────────────
    // Test-level: Create/close test node before and after each test
    // ─────────────────────────────────────────────────────────────

    @Override
    public void beforeEach(ExtensionContext context) {
        String testName = getTestName(context);
        String className = context.getTestClass().map(Class::getSimpleName).orElse("Unknown");

        log.info("------ Starting test: [{}] ------", testName);

        // Create a new Extent test node and store it in ThreadLocal
        ExtentTest test = ExtentReportManager.getInstance()
                .createTest(testName, "Class: " + className);

        // Apply tags as Extent categories
        context.getTags().forEach(test::assignCategory);

        ExtentTestManager.setTest(test);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        String testName = getTestName(context);
        log.info("------ Completed test: [{}] ------", testName);
        // Cleanup is handled by TestWatcher callbacks below
        ExtentTestManager.removeTest();
    }

    // ─────────────────────────────────────────────────────────────
    // TestWatcher: Handle individual test outcomes
    // ─────────────────────────────────────────────────────────────

    @Override
    public void testSuccessful(ExtensionContext context) {
        String testName = getTestName(context);
        log.info("✅ PASSED: [{}]", testName);
        ExtentTestManager.logPass("Test passed successfully.");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = getTestName(context);
        log.error("❌ FAILED: [{}] | Reason: {}", testName, cause.getMessage());

        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            // Attach failure exception
            test.log(Status.FAIL, "Test Failed: " + cause.getMessage());
            test.fail(cause);

            // Capture and embed screenshot
            String base64Screenshot = ScreenshotUtils.captureAsBase64();
            if (!base64Screenshot.isEmpty()) {
                try {
                    test.fail("Screenshot on failure:",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                } catch (Exception e) {
                    log.warn("Could not attach screenshot to report: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        String testName = getTestName(context);
        String disableReason = reason.orElse("No reason provided");
        log.warn("⏭️  SKIPPED: [{}] | Reason: {}", testName, disableReason);

        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.skip("Test skipped: " + disableReason);
        }
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        String testName = getTestName(context);
        log.warn("⚠️  ABORTED: [{}] | Reason: {}", testName, cause.getMessage());

        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.skip("Test aborted: " + cause.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────

    private String getTestName(ExtensionContext context) {
        return context.getTestMethod()
                .map(Method::getName)
                .orElse(context.getDisplayName());
    }
}
