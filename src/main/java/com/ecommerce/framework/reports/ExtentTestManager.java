package com.ecommerce.framework.reports;

import com.aventstack.extentreports.ExtentTest;

/**
 * ExtentTestManager — Thread-safe holder for individual test ExtentTest instances.
 *
 * <p>Design Pattern: ThreadLocal ensures each parallel test thread operates on its
 *    own ExtentTest node without cross-contaminating results from other threads.
 *
 * <p>Usage flow:
 * <ol>
 *   <li>TestListener calls setTest() when a test starts</li>
 *   <li>Page objects and utilities call getTest() to log steps</li>
 *   <li>TestListener calls removeTest() after the test completes</li>
 * </ol>
 */
public final class ExtentTestManager {

    /**
     * ThreadLocal storage for test-level ExtentTest instances.
     * Each thread (parallel test) maintains its own reference.
     */
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ExtentTestManager() {}

    /**
     * Returns the ExtentTest for the current thread.
     *
     * @return current thread's ExtentTest
     */
    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    /**
     * Sets the ExtentTest for the current thread.
     *
     * @param test the ExtentTest to store
     */
    public static void setTest(ExtentTest test) {
        testThreadLocal.set(test);
    }

    /**
     * Removes the ExtentTest from ThreadLocal.
     * MUST be called after each test to prevent memory leaks in thread pools.
     */
    public static void removeTest() {
        testThreadLocal.remove();
    }

    /**
     * Logs an INFO step to the current thread's ExtentTest.
     *
     * @param message step message
     */
    public static void logInfo(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.info(message);
        }
    }

    /**
     * Logs a PASS step to the current thread's ExtentTest.
     *
     * @param message step message
     */
    public static void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.pass(message);
        }
    }

    /**
     * Logs a FAIL step with a message to the current thread's ExtentTest.
     *
     * @param message failure message
     */
    public static void logFail(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.fail(message);
        }
    }

    /**
     * Logs a WARNING step to the current thread's ExtentTest.
     *
     * @param message warning message
     */
    public static void logWarning(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.warning(message);
        }
    }
}
